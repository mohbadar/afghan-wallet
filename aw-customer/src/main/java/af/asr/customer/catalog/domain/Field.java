
package af.asr.customer.catalog.domain;

import af.gov.anar.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class Field {

  public enum DataType {
    TEXT,
    NUMBER,
    DATE,
    SINGLE_SELECTION,
    MULTI_SELECTION
  }

  @ValidIdentifier
  private String identifier;
  @NotNull
  private DataType dataType;
  @NotEmpty
  private String label;
  private String hint;
  private String description;
  @Valid
  private List<Option> options;
  private Boolean mandatory;
  private Integer length;
  private Integer precision;
  private Double minValue;
  private Double maxValue;
  private String createdBy;
  private String createdOn;

  public Field() {
    super();
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(final String identifier) {
    this.identifier = identifier;
  }

  public String getDataType() {
    return this.dataType.name();
  }

  public void setDataType(final String dataType) {
    this.dataType = DataType.valueOf(dataType);
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getHint() {
    return this.hint;
  }

  public void setHint(final String hint) {
    this.hint = hint;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<Option> getOptions() {
    return this.options;
  }

  public void setOptions(final List<Option> options) {
    this.options = options;
  }

  public Boolean getMandatory() {
    return this.mandatory;
  }

  public void setMandatory(final Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public Integer getLength() {
    return this.length;
  }

  public void setLength(final Integer length) {
    this.length = length;
  }

  public Integer getPrecision() {
    return this.precision;
  }

  public void setPrecision(final Integer precision) {
    this.precision = precision;
  }

  public Double getMinValue() {
    return this.minValue;
  }

  public void setMinValue(final Double minValue) {
    this.minValue = minValue;
  }

  public Double getMaxValue() {
    return this.maxValue;
  }

  public void setMaxValue(final Double maxValue) {
    this.maxValue = maxValue;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final String createdOn) {
    this.createdOn = createdOn;
  }
}
