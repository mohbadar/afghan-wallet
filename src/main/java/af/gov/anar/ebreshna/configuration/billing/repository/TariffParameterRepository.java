package af.gov.anar.ebreshna.configuration.billing.repository;

import af.gov.anar.ebreshna.configuration.billing.model.TariffParameter;
import af.gov.anar.ebreshna.configuration.billing.model.TrLossesConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffParameterRepository extends JpaRepository<TariffParameter, Long> {
}
