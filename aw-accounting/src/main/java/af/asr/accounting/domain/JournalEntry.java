/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package af.asr.accounting.domain;

import af.gov.anar.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class JournalEntry {
  //This maxLength is the maximum message length plus a buffer.  portfolio places
  // the message here, plus the service name, and a random suffix.  This makes
  // it possible to match the transaction id on activemq messages used in integration
  // tests.
  @ValidIdentifier(maxLength = 2200)
  private String transactionIdentifier;
  @NotNull
  private String transactionDate;
  @ValidIdentifier
  private String transactionType;
  @NotEmpty
  private String clerk;
  private String note;
  @NotNull
  @Valid
  private Set<Debtor> debtors;
  @NotNull
  @Valid
  private Set<Creditor> creditors;
  private State state;
  @Length(max=2048)
  private String message;

  public JournalEntry() {
    super();
  }

  public String getTransactionIdentifier() {
    return this.transactionIdentifier;
  }

  public void setTransactionIdentifier(final String transactionIdentifier) {
    this.transactionIdentifier = transactionIdentifier;
  }

  public String getTransactionDate() {
    return this.transactionDate;
  }

  public void setTransactionDate(final String transactionDate) {
    this.transactionDate = transactionDate;
  }

  public String getTransactionType() {
    return this.transactionType;
  }

  public void setTransactionType(final String transactionType) {
    this.transactionType = transactionType;
  }

  public String getClerk() {
    return this.clerk;
  }

  public void setClerk(final String clerk) {
    this.clerk = clerk;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(final String note) {
    this.note = note;
  }

  public Set<Debtor> getDebtors() {
    return this.debtors;
  }

  public void setDebtors(final Set<Debtor> debtors) {
    this.debtors = debtors;
  }

  public Set<Creditor> getCreditors() {
    return this.creditors;
  }

  public void setCreditors(final Set<Creditor> creditors) {
    this.creditors = creditors;
  }

  public String getState() {
    return this.state.name();
  }

  public void setState(final String state) {
    this.state = State.valueOf(state);
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  @SuppressWarnings("WeakerAccess")
  public enum State {
    PENDING,
    PROCESSED
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JournalEntry that = (JournalEntry) o;
    return Objects.equals(transactionIdentifier, that.transactionIdentifier) &&
            Objects.equals(transactionDate, that.transactionDate) &&
            Objects.equals(transactionType, that.transactionType) &&
            Objects.equals(clerk, that.clerk) &&
            Objects.equals(note, that.note) &&
            Objects.equals(debtors, that.debtors) &&
            Objects.equals(creditors, that.creditors) &&
            state == that.state &&
            Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionIdentifier, transactionDate, transactionType, clerk, note, debtors, creditors, state, message);
  }

  @Override
  public String toString() {
    return "JournalEntry{" +
            "transactionIdentifier='" + transactionIdentifier + '\'' +
            ", transactionDate='" + transactionDate + '\'' +
            ", transactionType='" + transactionType + '\'' +
            ", clerk='" + clerk + '\'' +
            ", note='" + note + '\'' +
            ", debtors=" + debtors +
            ", creditors=" + creditors +
            ", state=" + state +
            ", message='" + message + '\'' +
            '}';
  }
}
