package af.gov.anar.ebreshna.common.network.repository;

import af.gov.anar.ebreshna.common.network.model.AreaMaster;
import af.gov.anar.ebreshna.common.network.model.FeederMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeederMasterRepository extends JpaRepository<FeederMaster, Long> {
}
