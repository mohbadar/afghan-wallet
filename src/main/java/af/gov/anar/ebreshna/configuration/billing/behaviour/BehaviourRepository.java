package af.gov.anar.ebreshna.configuration.billing.behaviour;

import af.gov.anar.ebreshna.configuration.billing.behaviour.BehaviourConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BehaviourRepository extends JpaRepository<BehaviourConfiguration, Long> {
}
