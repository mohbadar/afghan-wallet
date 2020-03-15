
package af.asr.payroll.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "payroll_payroll_payments", schema = "payroll")
public class PayrollPaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "payroll_collection_id", nullable = false)
  private PayrollCollectionEntity payrollCollection;
  @Column(name = "customer_identifier", nullable = false, length = 32)
  private String customerIdentifier;
  @Column(name = "employer", nullable = false, length = 256)
  private String employer;
  @Column(name = "salary", nullable = false, precision = 15, scale = 5)
  private BigDecimal salary;
  @Column(name = "processed", nullable = false)
  private Boolean processed;
  @Column(name = "message", nullable = true)
  private String message;

  public PayrollPaymentEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public PayrollCollectionEntity getPayrollCollection() {
    return this.payrollCollection;
  }

  public void setPayrollCollection(final PayrollCollectionEntity payrollCollection) {
    this.payrollCollection = payrollCollection;
  }

  public String getCustomerIdentifier() {
    return this.customerIdentifier;
  }

  public void setCustomerIdentifier(final String customerIdentifier) {
    this.customerIdentifier = customerIdentifier;
  }

  public String getEmployer() {
    return this.employer;
  }

  public void setEmployer(final String employer) {
    this.employer = employer;
  }

  public BigDecimal getSalary() {
    return this.salary;
  }

  public void setSalary(final BigDecimal salary) {
    this.salary = salary;
  }

  public Boolean getProcessed() {
    return this.processed;
  }

  public void setProcessed(final Boolean processed) {
    this.processed = processed;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }
}
