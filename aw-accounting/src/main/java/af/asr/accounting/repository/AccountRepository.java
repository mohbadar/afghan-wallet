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
package af.asr.accounting.repository;

import af.asr.accounting.model.AccountEntity;
import af.asr.accounting.model.LedgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor<AccountEntity> {

  List<AccountEntity> findByLedger(final LedgerEntity ledgerEntity);

  Page<AccountEntity> findByLedger(final LedgerEntity ledgerEntity, final Pageable pageable);

  AccountEntity findByIdentifier(final String identifier);

  @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM AccountEntity a where a.referenceAccount = :accountEntity")
  Boolean existsByReference(@Param("accountEntity") final AccountEntity accountEntity);

  Stream<AccountEntity> findByBalanceIsNot(final Double value);
}
