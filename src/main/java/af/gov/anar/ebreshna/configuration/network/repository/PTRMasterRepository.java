package af.gov.anar.ebreshna.configuration.network.repository;

import af.gov.anar.ebreshna.configuration.network.model.PTRMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PTRMasterRepository extends JpaRepository<PTRMaster, Long> {
}
