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
package af.asr.csc.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.lang.DateConverter;
import org.apache.fineract.cn.lang.DateOfBirth;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDateTime;

public final class CustomerMapper {

  private CustomerMapper() {
    super();
  }

  public static CustomerEntity map(final Customer customer) {
    final CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setIdentifier(customer.getIdentifier());
    customerEntity.setType(customer.getType());
    customerEntity.setGivenName(customer.getGivenName());
    customerEntity.setMiddleName(customer.getMiddleName());
    customerEntity.setSurname(customer.getSurname());
    customerEntity.setDateOfBirth(Date.valueOf(customer.getDateOfBirth().toLocalDate()));
    customerEntity.setMember(customer.getMember());
    customerEntity.setAccountBeneficiary(customer.getAccountBeneficiary());
    customerEntity.setReferenceCustomer(customer.getReferenceCustomer());
    customerEntity.setAssignedOffice(customer.getAssignedOffice());
    customerEntity.setAssignedEmployee(customer.getAssignedEmployee());
    customerEntity.setCurrentState(customer.getCurrentState());
    if (customer.getApplicationDate() != null) {
      final String editedApplicationDate;
      if (!customer.getApplicationDate().endsWith("Z")) {
        editedApplicationDate = customer.getApplicationDate() + "Z";
      } else {
        editedApplicationDate = customer.getApplicationDate();
      }
      customerEntity.setApplicationDate(DateConverter.dateFromIsoString(editedApplicationDate));
    }
    customerEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    customerEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    return customerEntity;
  }

  public static Customer map(final CustomerEntity customerEntity) {
    final Customer customer = new Customer();
    customer.setIdentifier(customerEntity.getIdentifier());
    customer.setType(customerEntity.getType());
    customer.setGivenName(customerEntity.getGivenName());
    customer.setMiddleName(customerEntity.getMiddleName());
    customer.setSurname(customerEntity.getSurname());
    customer.setDateOfBirth(DateOfBirth.fromLocalDate(customerEntity.getDateOfBirth().toLocalDate()));
    customer.setMember(customerEntity.getMember());
    customer.setAccountBeneficiary(customerEntity.getAccountBeneficiary());
    customer.setReferenceCustomer(customerEntity.getReferenceCustomer());
    customer.setAssignedOffice(customerEntity.getAssignedOffice());
    customer.setAssignedEmployee(customerEntity.getAssignedEmployee());
    customer.setCurrentState(customerEntity.getCurrentState());
    if (customerEntity.getApplicationDate() != null) {
      final String editedApplicationDate =
          DateConverter.toIsoString(customerEntity.getApplicationDate()).substring(0, 10);
      customer.setApplicationDate(editedApplicationDate);
    }
    customer.setCreatedBy(customerEntity.getCreatedBy());
    customer.setCreatedOn(DateConverter.toIsoString(customerEntity.getCreatedOn()));

    if (customerEntity.getLastModifiedBy() != null) {
      customer.setLastModifiedBy(customerEntity.getLastModifiedBy());
      customer.setLastModifiedOn(DateConverter.toIsoString(customerEntity.getLastModifiedOn()));
    }

    return customer;
  }
}
