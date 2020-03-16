
package af.asr.accounting.model;


import af.asr.infrastructure.converter.LocalDateTimeConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuppressWarnings({"unused"})
@Entity
@Table(name = "acc_ledgers", schema = "accounting")
public class LedgerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "a_type")
  private String type;
  @Column(name = "identifier")
  private String identifier;
  @Column(name = "a_name")
  private String name;
  @Column(name = "description")
  private String description;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_ledger_id")
  private LedgerEntity parentLedger;
  @Column(name = "total_value")
  private BigDecimal totalValue;
  @Column(name = "created_on")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime createdOn;
  @Column(name = "created_by")
  private String createdBy;
  @Column(name = "last_modified_on")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime lastModifiedOn;
  @Column(name = "last_modified_by")
  private String lastModifiedBy;
  @Column(name = "show_accounts_in_chart")
  private Boolean showAccountsInChart;

  public LedgerEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(final String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public LedgerEntity getParentLedger() {
    return this.parentLedger;
  }

  public void setParentLedger(final LedgerEntity parentLedger) {
    this.parentLedger = parentLedger;
  }

  public BigDecimal getTotalValue() {
    return this.totalValue;
  }

  public void setTotalValue(final BigDecimal totalValue) {
    this.totalValue = totalValue;
  }

  public LocalDateTime getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getLastModifiedOn() {
    return this.lastModifiedOn;
  }

  public void setLastModifiedOn(final LocalDateTime lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(final String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Boolean getShowAccountsInChart() {
    return this.showAccountsInChart;
  }

  public void setShowAccountsInChart(final Boolean showAccountsInChart) {
    this.showAccountsInChart = showAccountsInChart;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final LedgerEntity that = (LedgerEntity) o;

    return identifier.equals(that.identifier);

  }

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }
}
