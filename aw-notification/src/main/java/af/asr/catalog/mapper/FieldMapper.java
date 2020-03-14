
package af.asr.catalog.mapper;


import af.asr.catalog.domain.Field;
import af.asr.catalog.model.CatalogEntity;
import af.asr.catalog.model.FieldEntity;
import af.gov.anar.lang.validation.date.DateConverter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class FieldMapper {

  private FieldMapper() {
    super();
  }

  public static FieldEntity map(final CatalogEntity catalogEntity, final Field field) {
    final FieldEntity fieldEntity = new FieldEntity();
    fieldEntity.setCatalog(catalogEntity);
    fieldEntity.setIdentifier(field.getIdentifier());
    fieldEntity.setLabel(field.getLabel());
    fieldEntity.setHint(field.getHint());
    fieldEntity.setDescription(field.getDescription());
    fieldEntity.setDataType(field.getDataType());
    fieldEntity.setMandatory(field.getMandatory());
    fieldEntity.setLength(field.getLength());
    fieldEntity.setPrecision(field.getPrecision());
    fieldEntity.setMinValue(field.getMinValue());
    fieldEntity.setMaxValue(field.getMaxValue());
//    fieldEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    fieldEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));

    if (field.getOptions() != null && !field.getOptions().isEmpty()) {
      fieldEntity.setOptions(field.getOptions()
          .stream()
          .map(option -> OptionMapper.map(fieldEntity, option))
          .collect(Collectors.toList())
      );
    }

    return fieldEntity;
  }

  public static Field map(final FieldEntity fieldEntity) {
    final Field field = new Field();
    field.setIdentifier(fieldEntity.getIdentifier());
    field.setLabel(fieldEntity.getLabel());
    field.setHint(fieldEntity.getHint());
    field.setDescription(fieldEntity.getDescription());
    field.setDataType(fieldEntity.getDataType());
    field.setMandatory(fieldEntity.getMandatory());
    field.setLength(fieldEntity.getLength());
    field.setPrecision(fieldEntity.getPrecision());
    field.setMinValue(fieldEntity.getMinValue());
    field.setMaxValue(fieldEntity.getMaxValue());
    field.setCreatedBy(fieldEntity.getCreatedBy());
    field.setCreatedOn(DateConverter.toIsoString(fieldEntity.getCreatedOn()));

    if (fieldEntity.getOptions() != null && !fieldEntity.getOptions().isEmpty()) {
      field.setOptions(
          fieldEntity.getOptions()
              .stream()
              .map(OptionMapper::map)
              .collect(Collectors.toList())
      );
    }

    return field;
  }
}
