
package af.asr.catalog.service;

import af.asr.catalog.domain.Catalog;
import af.asr.catalog.mapper.CatalogMapper;
import af.asr.catalog.mapper.FieldMapper;
import af.asr.catalog.model.CatalogEntity;
import af.asr.catalog.model.FieldEntity;
import af.asr.catalog.repository.CatalogRepository;
import af.asr.catalog.repository.FieldRepository;
import af.asr.catalog.repository.FieldValueRepository;
import af.asr.csc.util.ServiceConstants;
import af.gov.anar.lang.infrastructure.exception.service.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogService {

  private final Logger logger;
  private final CatalogRepository catalogRepository;
  private final FieldRepository fieldRepository;
  private final FieldValueRepository fieldValueRepository;

  @Autowired
  public CatalogService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                        final CatalogRepository catalogRepository,
                        final FieldRepository fieldRepository,
                        final FieldValueRepository fieldValueRepository) {
    super();
    this.logger = logger;
    this.catalogRepository = catalogRepository;
    this.fieldRepository = fieldRepository;
    this.fieldValueRepository = fieldValueRepository;
  }

  public Boolean catalogExists(final String identifier) {
    return this.catalogRepository.findByIdentifier(identifier).isPresent();
  }

  public List<Catalog> fetchAllCatalogs() {
    return this.catalogRepository.findAll()
        .stream()
        .map(catalogEntity -> {
          final Catalog catalog = CatalogMapper.map(catalogEntity);
          catalog.setFields(
              catalogEntity.getFields()
                  .stream()
                  .map(FieldMapper::map)
                  .collect(Collectors.toList())
          );
          return catalog;
        })
        .collect(Collectors.toList());
  }

  public Optional<Catalog> findCatalog(final String identifier) {
    return this.catalogRepository.findByIdentifier(identifier)
        .map(catalogEntity -> {
          final Catalog catalog = CatalogMapper.map(catalogEntity);
          catalog.setFields(
              catalogEntity.getFields()
                  .stream()
                  .map(FieldMapper::map)
                  .collect(Collectors.toList())
          );
          return catalog;
        });
  }

  public Boolean catalogInUse(final String identifier) {
    final CatalogEntity catalogEntity = this.catalogRepository.findByIdentifier(identifier).orElseThrow(
        () -> ServiceException.notFound("Catalog {0} not found.", identifier)
    );

    final ArrayList<Boolean> fieldUsageList = new ArrayList<>();
    catalogEntity.getFields().forEach(fieldEntity -> {
      fieldUsageList.add(this.fieldInUse(catalogEntity, fieldEntity.getIdentifier()));
    });

    return fieldUsageList.stream().anyMatch(aBoolean -> aBoolean.equals(Boolean.TRUE));
  }

  public Boolean fieldInUse(final String catalogIdentifier, final String fieldIdentifier) {
    final CatalogEntity catalogEntity = this.catalogRepository.findByIdentifier(catalogIdentifier).orElseThrow(
        () -> ServiceException.notFound("Catalog {0} not found.", catalogIdentifier)
    );
    return this.fieldInUse(catalogEntity, fieldIdentifier);
  }

  private Boolean fieldInUse(final CatalogEntity catalogEntity, final String fieldIdentifier) {
    final FieldEntity fieldEntity = this.fieldRepository.findByCatalogAndIdentifier(catalogEntity, fieldIdentifier).orElseThrow(
        () -> ServiceException.notFound("Field {0} of catalog {1} not found.", catalogEntity.getIdentifier(), fieldIdentifier));
    return this.fieldValueRepository.findByField(fieldEntity).isPresent();
  }
}
