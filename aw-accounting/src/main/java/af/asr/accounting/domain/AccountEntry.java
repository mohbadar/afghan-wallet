
package af.asr.accounting.domain;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class AccountEntry {

  private Type type;
  private String transactionDate;
  private String message;
  private Double amount;
  private Double balance;

  public String getType() {
    return this.type.name();
  }

  public void setType(final String type) {
    this.type = Type.valueOf(type);
  }

  public String getTransactionDate() {
    return this.transactionDate;
  }

  public void setTransactionDate(final String transactionDate) {
    this.transactionDate = transactionDate;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(final Double amount) {
    this.amount = amount;
  }

  public Double getBalance() {
    return this.balance;
  }

  public void setBalance(final Double balance) {
    this.balance = balance;
  }

  public enum Type {
    DEBIT,
    CREDIT
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountEntry that = (AccountEntry) o;
    return type == that.type &&
        Objects.equals(transactionDate, that.transactionDate) &&
        Objects.equals(message, that.message) &&
        Objects.equals(amount, that.amount) &&
        Objects.equals(balance, that.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, transactionDate, message, amount, balance);
  }
}
