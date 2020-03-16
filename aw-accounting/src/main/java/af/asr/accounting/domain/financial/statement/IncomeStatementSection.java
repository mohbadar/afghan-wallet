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
package af.asr.accounting.domain.financial.statement;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IncomeStatementSection {

  public enum Type {
    INCOME,
    EXPENSES
  }

  @NotEmpty
  private Type type;
  @NotEmpty
  private String description;
  @NotEmpty
  private List<IncomeStatementEntry> incomeStatementEntries = new ArrayList<>();
  @NotNull
  private BigDecimal subtotal = BigDecimal.ZERO;

  public IncomeStatementSection() {
    super();
  }

  public String getType() {
    return this.type.name();
  }

  public void setType(final String type) {
    this.type = Type.valueOf(type);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<IncomeStatementEntry> getIncomeStatementEntries() {
    return this.incomeStatementEntries;
  }

  public BigDecimal getSubtotal() {
    return this.subtotal;
  }

  public void add(final IncomeStatementEntry incomeStatementEntry) {
    this.incomeStatementEntries.add(incomeStatementEntry);
    this.subtotal = this.subtotal.add(incomeStatementEntry.getValue());
  }
}
