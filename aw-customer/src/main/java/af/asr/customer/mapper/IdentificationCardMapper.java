
package af.asr.customer.mapper;

import af.asr.customer.domain.ExpirationDate;
import af.asr.customer.domain.IdentificationCard;
import af.asr.customer.model.IdentificationCardEntity;
import af.gov.anar.lang.validation.date.DateConverter;
import org.springframework.stereotype.Component;

@Component
public final class IdentificationCardMapper {

  private IdentificationCardMapper() {
    super();
  }

  public static IdentificationCardEntity map(final IdentificationCard identificationCard) {
    final IdentificationCardEntity identificationCardEntity = new IdentificationCardEntity();
    identificationCardEntity.setType(identificationCard.getType());
    identificationCardEntity.setNumber(identificationCard.getNumber());
    identificationCardEntity.setExpirationDate(identificationCard.getExpirationDate().toLocalDate());
    identificationCardEntity.setIssuer(identificationCard.getIssuer());
    return identificationCardEntity;
  }

  public static IdentificationCard map(final IdentificationCardEntity identificationCardEntity) {
    final IdentificationCard identificationCard = new IdentificationCard();
    identificationCard.setType(identificationCardEntity.getType());
    identificationCard.setNumber(identificationCardEntity.getNumber());
    identificationCard.setExpirationDate(ExpirationDate.fromLocalDate(identificationCardEntity.getExpirationDate()));
    identificationCard.setIssuer(identificationCardEntity.getIssuer());

    if (identificationCardEntity.getCreatedBy() != null ) {
      identificationCard.setCreatedBy(identificationCardEntity.getCreatedBy());
      identificationCard.setCreatedOn(DateConverter.toIsoString(identificationCardEntity.getCreatedOn()));
    }

    if (identificationCardEntity.getLastModifiedBy() != null) {
      identificationCard.setLastModifiedBy(identificationCardEntity.getLastModifiedBy());
      identificationCard.setLastModifiedOn(DateConverter.toIsoString(identificationCardEntity.getLastModifiedOn()));
    }

    return identificationCard;
  }
}
