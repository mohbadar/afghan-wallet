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
package af.asr.office.service;

import af.asr.office.ServiceConstants;
import af.asr.office.domain.Address;
import af.asr.office.domain.ExternalReference;
import af.asr.office.domain.Office;
import af.asr.office.domain.OfficePage;
import af.asr.office.mapper.AddressMapper;
import af.asr.office.mapper.OfficeMapper;
import af.asr.office.model.AddressEntity;
import af.asr.office.model.OfficeEntity;
import af.asr.office.repository.AddressRepository;
import af.asr.office.repository.EmployeeRepository;
import af.asr.office.repository.ExternalReferenceRepository;
import af.asr.office.repository.OfficeRepository;
import af.gov.anar.lang.infrastructure.exception.service.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OfficeService {

  private final Logger logger;
  private final OfficeRepository officeRepository;
  private final AddressRepository addressRepository;
  private final EmployeeRepository employeeRepository;
  private final ExternalReferenceRepository externalReferenceRepository;

  @Autowired
  public OfficeService(@Qualifier(ServiceConstants.SERVICE_LOGGER_NAME) final Logger logger,
                       final OfficeRepository officeRepository,
                       final AddressRepository addressRepository,
                       final EmployeeRepository employeeRepository,
                       final ExternalReferenceRepository externalReferenceRepository) {
    super();
    this.logger = logger;
    this.officeRepository = officeRepository;
    this.addressRepository = addressRepository;
    this.employeeRepository = employeeRepository;
    this.externalReferenceRepository = externalReferenceRepository;
  }

  public boolean officeExists(final String identifier) {
    return this.officeRepository.existsByIdentifier(identifier);
  }

  public boolean branchExists(final String identifier) {
    final Optional<OfficeEntity> officeEntityOptional = this.officeRepository.findByIdentifier(identifier);
    return officeEntityOptional.map(officeEntity -> this.officeRepository.existsByParentOfficeId(officeEntity.getId())).orElse(false);
  }

  public boolean hasEmployees(final String officeIdentifier){
    return this.officeRepository.findByIdentifier(officeIdentifier)
            .map(this.employeeRepository::existsByAssignedOffice)
            .orElse(false);
  }

  @Transactional(readOnly = true)
  public OfficePage fetchOffices(final String term, final Pageable pageRequest) {
    final Page<OfficeEntity> officeEntityPage;
    if (term != null) {
      officeEntityPage = this.officeRepository.findByIdentifierContainingOrNameContaining(term, term, pageRequest);
    } else {
      officeEntityPage = this.officeRepository.findByParentOfficeIdIsNull(pageRequest);
    }

    final OfficePage officePage = new OfficePage();
    officePage.setTotalPages(officeEntityPage.getTotalPages());
    officePage.setTotalElements(officeEntityPage.getTotalElements());
    officePage.setOffices(this.extractOfficeEntities(officeEntityPage, null));

    return officePage;
  }

  public Optional<Office> findOfficeByIdentifier(final String identifier) {
    final Optional<OfficeEntity> officeEntityOptional = this.officeRepository.findByIdentifier(identifier);

    if (officeEntityOptional.isPresent()) {
      final Optional<Office> officeOptional = officeEntityOptional.map(OfficeMapper::map);

      officeOptional.ifPresent(office -> {
        final Long parentOfficeId = officeEntityOptional.get().getParentOfficeId();
        if(parentOfficeId != null) {
          final OfficeEntity parentEntity = this.officeRepository.getOne(parentOfficeId);
          office.setParentIdentifier(parentEntity.getIdentifier());
        }

        final Optional<AddressEntity> addressEntityOptional = this.addressRepository.findByOffice(officeEntityOptional.get());
        addressEntityOptional.ifPresent(addressEntity -> office.setAddress(AddressMapper.map(addressEntity)));

        office.setExternalReferences(
            this.branchExists(office.getIdentifier())
                || this.hasEmployees(office.getIdentifier())
                || this.hasExternalReferences(office.getIdentifier())
        );
      });

      return officeOptional;
    }
    return Optional.empty();
  }

  public Optional<Address> findAddressOfOffice(final String identifier) {
    final Optional<OfficeEntity> officeEntityOptional = this.officeRepository.findByIdentifier(identifier);

    if (!officeEntityOptional.isPresent()) {
      throw ServiceException.notFound("Office {0} not found.", identifier);
    }

    final Optional<AddressEntity> addressEntityOptional = this.addressRepository.findByOffice(officeEntityOptional.get());

    return addressEntityOptional.map(AddressMapper::map);
  }

  @Transactional(readOnly = true)
  public OfficePage fetchBranches(final String parentIdentifier, final Pageable pageRequest) {
    final OfficeEntity parentOfficeEntity = this.officeRepository.findByIdentifier(parentIdentifier)
        .orElseThrow(() -> ServiceException.notFound("Parent office {0} not found!", parentIdentifier));

    final Page<OfficeEntity> officeEntityPage = this.officeRepository.findByParentOfficeId(parentOfficeEntity.getId(), pageRequest);
    final OfficePage officePage = new OfficePage();
    officePage.setTotalPages(officeEntityPage.getTotalPages());
    officePage.setTotalElements(officeEntityPage.getTotalElements());
    officePage.setOffices(this.extractOfficeEntities(officeEntityPage, parentIdentifier));

    return officePage;
  }

  public List<Office> extractOfficeEntities(final Page<OfficeEntity> officeEntityPage, final String parentIdentifier) {
    final List<Office> offices = new ArrayList<>(officeEntityPage.getSize());
    officeEntityPage.forEach(officeEntity -> {
      final Office office = OfficeMapper.map(officeEntity);
      if (parentIdentifier != null) {
        office.setParentIdentifier(parentIdentifier);
      }
      offices.add(office);

      final Optional<AddressEntity> addressEntityOptional = this.addressRepository.findByOffice(officeEntity);
      addressEntityOptional.ifPresent(addressEntity -> office.setAddress(AddressMapper.map(addressEntity)));

      office.setExternalReferences(
          this.branchExists(office.getIdentifier())
              || this.hasEmployees(office.getIdentifier())
              || this.hasExternalReferences(office.getIdentifier())
      );
    });
    return offices;
  }

  public boolean hasExternalReferences(final String officeIdentifier) {
    return this.externalReferenceRepository.findByOfficeIdentifier(officeIdentifier)
        .stream()
        .anyMatch(externalReferenceEntity ->
            externalReferenceEntity.getState().equals(ExternalReference.State.ACTIVE.name()));
  }
}
