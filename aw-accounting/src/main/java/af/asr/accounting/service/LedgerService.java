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
package af.asr.accounting.service;

import af.asr.accounting.repository.AccountRepository;
import af.asr.accounting.repository.LedgerRepository;
import af.asr.accounting.domain.*;
import af.asr.accounting.domain.financial.statement.*;
import af.asr.accounting.mapper.*;
import af.asr.accounting.model.*;
import af.asr.accounting.repository.*;
import af.asr.accounting.specification.LedgerSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LedgerService {

  private final LedgerRepository ledgerRepository;
  private final AccountRepository accountRepository;

  @Autowired
  public LedgerService(final LedgerRepository ledgerRepository,
                       final AccountRepository accountRepository) {
    super();
    this.ledgerRepository = ledgerRepository;
    this.accountRepository = accountRepository;
  }

  public LedgerPage fetchLedgers(final boolean includeSubLedgers,
                                 final String term,
                                 final String type,
                                 final Pageable pageable) {
    final LedgerPage ledgerPage = new LedgerPage();

    final Page<LedgerEntity> ledgerEntities = this.ledgerRepository.findAll(
        LedgerSpecification.createSpecification(includeSubLedgers, term, type), pageable
    );

    ledgerPage.setTotalPages(ledgerEntities.getTotalPages());
    ledgerPage.setTotalElements(ledgerEntities.getTotalElements());

    ledgerPage.setLedgers(this.mapToLedger(ledgerEntities.getContent()));

    return ledgerPage;
  }

  private List<Ledger> mapToLedger(List<LedgerEntity> ledgerEntities) {
    final List<Ledger> result = new ArrayList<>(ledgerEntities.size());

    if(!ledgerEntities.isEmpty()) {
      ledgerEntities.forEach(ledgerEntity -> {
        final Ledger ledger = LedgerMapper.map(ledgerEntity);
        this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity));
        result.add(ledger);
      });
    }

    return result;
  }

  public Optional<Ledger> findLedger(final String identifier) {
    final LedgerEntity ledgerEntity = this.ledgerRepository.findByIdentifier(identifier);
    if (ledgerEntity != null) {
      final Ledger ledger = LedgerMapper.map(ledgerEntity);
      this.addSubLedgers(ledger, this.ledgerRepository.findByParentLedgerOrderByIdentifier(ledgerEntity));
      return Optional.of(ledger);
    } else {
      return Optional.empty();
    }
  }

  public AccountPage fetchAccounts(final String ledgerIdentifier, final Pageable pageable) {
    final LedgerEntity ledgerEntity = this.ledgerRepository.findByIdentifier(ledgerIdentifier);
    final Page<AccountEntity> accountEntities = this.accountRepository.findByLedger(ledgerEntity, pageable);

    final AccountPage accountPage = new AccountPage();
    accountPage.setTotalPages(accountEntities.getTotalPages());
    accountPage.setTotalElements(accountEntities.getTotalElements());

    if(accountEntities.getSize() > 0){
      final List<Account> accounts = new ArrayList<>(accountEntities.getSize());
      accountEntities.forEach(accountEntity -> accounts.add(AccountMapper.map(accountEntity)));
      accountPage.setAccounts(accounts);
    }

    return accountPage;
  }

  public boolean hasAccounts(final String ledgerIdentifier) {
    final LedgerEntity ledgerEntity = this.ledgerRepository.findByIdentifier(ledgerIdentifier);
    final List<AccountEntity> ledgerAccounts = this.accountRepository.findByLedger(ledgerEntity);
    return ledgerAccounts.size() > 0;
  }

  private void addSubLedgers(final Ledger parentLedger,
                             final List<LedgerEntity> subLedgerEntities) {
    if (subLedgerEntities != null) {
      final List<Ledger> subLedgers = new ArrayList<>(subLedgerEntities.size());
      subLedgerEntities.forEach(subLedgerEntity -> subLedgers.add(LedgerMapper.map(subLedgerEntity)));
      parentLedger.setSubLedgers(subLedgers);
    }
  }
}
