package af.gov.anar.ebreshna.common.network.repository;

import af.gov.anar.ebreshna.common.network.model.AreaMaster;
import af.gov.anar.ebreshna.common.network.model.DistributionTransformerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionTransformerMasterRepository extends JpaRepository<DistributionTransformerMaster, Long> {
}
