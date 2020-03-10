package af.gov.anar.lib.businessworkingcalendar.expression;

import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A representation of cron expressions.
 */
public class CronExpression {

    private static TemporalField[] CRON_FIELDS = {
            ChronoField.MINUTE_OF_HOUR,
            ChronoField.HOUR_OF_DAY,
            ChronoField.DAY_OF_MONTH,
            ChronoField.MONTH_OF_YEAR,
            ChronoField.DAY_OF_WEEK};

    private final Map<TemporalField, SortedSet<Integer>> fieldValues;

    /**
     * Create a CronExpression that triggers when the given temporal occurs.
     * Only the following fields of the temporal (if they are supported) are
     * supported:
     * <ul>
     * <li>{@link ChronoField#MINUTE_OF_HOUR}</li>
     * <li>{@link ChronoField#HOUR_OF_DAY}</li>
     * <li>{@link ChronoField#DAY_OF_MONTH}</li>
     * <li>{@link ChronoField#MONTH_OF_YEAR}</li>
     * <li>{@link ChronoField#DAY_OF_WEEK}</li>
     * </ul>
     *
     * @param temporal the temporal from which to build the cron expression
     */
    public CronExpression(Temporal temporal) {

        //get the field values that are supported by the temporal
        fieldValues = new HashMap<>(CRON_FIELDS.length);
        long minSupportedUnitDuration = Long.MAX_VALUE;
        List<TemporalField> unsupportedFields = new ArrayList<>(CRON_FIELDS.length);
        for (TemporalField field : CRON_FIELDS) {
            if (temporal.isSupported(field)) {
                fieldValues.put(field, new TreeSet<>(Collections.singleton(temporal.get(field))));
                minSupportedUnitDuration = BinaryOperator.<Long>minBy(Comparator.naturalOrder())
                        .apply(minSupportedUnitDuration, field.getBaseUnit().getDuration().getSeconds());
            } else {
                unsupportedFields.add(field);
            }
        }

        //fields not supported by the temporal: either any value or only the minimum value is acceptable,
        //depending on which side of the supported fields it is
        for (TemporalField field : unsupportedFields) {
            fieldValues.put(field, new TreeSet<>(
                    (field.getBaseUnit().getDuration().getSeconds() > minSupportedUnitDuration
                            ? LongStream.rangeClosed(field.range().getMinimum(), field.range().getMaximum())
                            : LongStream.of(field.range().getMinimum()))
                            .mapToInt(value -> field.range().checkValidIntValue(value, field))
                            .mapToObj(Integer::valueOf)
                            .collect(Collectors.toSet())));
        }
    }

    private CronExpression(Map<TemporalField, SortedSet<Integer>> fieldValues) {
        this.fieldValues = Objects.requireNonNull(fieldValues);
    }

    private boolean canMergeWith(CronExpression other) {
        //Two crons can be merged if there is at most one difference between them
        return fieldValues
                .entrySet()
                .stream()
                .mapToInt(entry -> other.fieldValues.get(entry.getKey()).equals(entry.getValue()) ? 0 : 1)
                .sum() <= 1;
    }

    private CronExpression merge(CronExpression other) {
        //merge the corresponding fields
        return new CronExpression(
                fieldValues
                        .entrySet()
                        .stream()
                        .collect(Collectors.<Map.Entry<TemporalField, SortedSet<Integer>>, TemporalField, SortedSet<Integer>>toMap(
                                Map.Entry::getKey,
                                entry -> Stream.concat(
                                        entry.getValue().stream(),
                                        other.fieldValues.get(entry.getKey()).stream())
                                        .collect(Collectors.toCollection(TreeSet::new)))));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return fieldValues.hashCode();
    }

    /**
     * Tells if this cron expression is equals to the given one.
     * Two cron expresions are equals if they trigger at exactly the same instants.
     * @param obj the object to this expression compare to
     * @return true if the expressions are equals, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return Optional
                .ofNullable(obj)
                .filter(CronExpression.class::isInstance)
                .filter(other -> fieldValues.equals(((CronExpression) other).fieldValues))
                .isPresent();
    }

    /**
     * Convert this cron expression into its string representation.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Cron#CRON_expression">the
     * cron expression format</a>
     * @return the string representation of this cron expression
     */
    @Override
    public String toString() {
        return Arrays.stream(CRON_FIELDS)
                .map(field -> fieldToString(field.range(), fieldValues.get(field)))
                .collect(Collectors.joining(" "));
    }

    private static String fieldToString(ValueRange range, SortedSet<Integer> fieldValues) {
        return fieldValues.first().equals(range.checkValidIntValue(range.getMinimum(), null))
                && fieldValues.last().equals(range.checkValidIntValue(range.getMaximum(), null)) ? "*"
                : toRanges(fieldValues)
                .stream()
                .map(CronExpression::rangeToString)
                .collect(Collectors.joining(","));
    }

    private static Set<ValueRange> toRanges(SortedSet<Integer> fieldValues) {
        Set<ValueRange> ranges = new HashSet<>();
        ValueRange currentRange = null;
        for (Integer value : fieldValues) {
            if (currentRange == null) {
                currentRange = ValueRange.of(value, value);
            } else if (currentRange.getMaximum() == value - 1) {
                currentRange = ValueRange.of(currentRange.getMinimum(), value);
            } else {
                ranges.add(currentRange);
                currentRange = ValueRange.of(value, value);
            }
        }
        ranges.add(currentRange);
        return ranges;
    }

    private static String rangeToString(ValueRange range) {
        return range.getMinimum() == range.getMaximum()
                ? String.valueOf(range.getMinimum())
                : range.getMinimum() + "-" + range.getMaximum();
    }

    /**
     * Merge the given cron expressions where it is possible.
     * For instance, <code>"2 18 * * *"</code> and <code>"4 18 * * *"</code>
     * can be merged in <code>"2,4 18 * * *"</code>.
     * @param crons the cron expressions to merge
     * @return the merged cron expressions.
     * They will trigger at the exact same moments as the input cron expressions.
     */
    public static Set<CronExpression> merge(Collection<CronExpression> crons) {
        Set<CronExpression> mergedCrons = new HashSet<>(crons.size());
        for (CronExpression cronToMerge : crons) {
            CronExpression mergeWith = null;
            for (CronExpression cron : mergedCrons) {
                if (cron.canMergeWith(cronToMerge)) {
                    mergeWith = cron;
                    break;
                }
            }

            if (mergeWith == null) {
                mergedCrons.add(cronToMerge);
            } else {
                mergedCrons.remove(mergeWith);
                mergedCrons.add(mergeWith.merge(cronToMerge));
            }
        }
        return mergedCrons;
    }

}
