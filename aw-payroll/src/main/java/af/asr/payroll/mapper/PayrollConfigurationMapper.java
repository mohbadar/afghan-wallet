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
package af.asr.payroll.mapper;


import af.asr.payroll.domain.PayrollConfiguration;
import af.asr.payroll.model.PayrollConfigurationEntity;
import af.gov.anar.lang.validation.date.DateConverter;

public class PayrollConfigurationMapper {

  private PayrollConfigurationMapper() {
    super();
  }

  public static PayrollConfiguration map(final PayrollConfigurationEntity payrollConfigurationEntity) {
    final PayrollConfiguration payrollConfiguration = new PayrollConfiguration();
    payrollConfiguration.setMainAccountNumber(payrollConfigurationEntity.getMainAccountNumber());
    payrollConfiguration.setCreatedBy(payrollConfigurationEntity.getCreatedBy());
    payrollConfiguration.setCreatedOn(DateConverter.toIsoString(payrollConfigurationEntity.getCreatedOn()));
    if (payrollConfigurationEntity.getLastModifiedBy() != null) {
      payrollConfiguration.setLastModifiedBy(payrollConfigurationEntity.getLastModifiedBy());
      payrollConfiguration.setLastModifiedOn(DateConverter.toIsoString(payrollConfigurationEntity.getLastModifiedOn()));
    }
    return payrollConfiguration;
  }
}
