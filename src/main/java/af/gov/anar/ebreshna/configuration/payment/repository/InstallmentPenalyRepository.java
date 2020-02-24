package af.gov.anar.ebreshna.configuration.payment.repository;

import af.gov.anar.ebreshna.configuration.payment.model.InstallmentPenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentPenalyRepository extends JpaRepository<InstallmentPenalty, Long> {
}
