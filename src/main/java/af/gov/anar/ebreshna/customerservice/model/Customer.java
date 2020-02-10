package af.gov.anar.ebreshna.customerservice.model;

import af.gov.anar.ebreshna.common.base.BaseEntity;
import af.gov.anar.ebreshna.office.model.PremisesCategory;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "crm_customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Audited
public class Customer extends BaseEntity {

    @ManyToOne(targetEntity = PremisesCategory.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "request_type_id")
    private RequestType requestType;

    @ManyToOne(targetEntity = PremisesCategory.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "premises_category_id")
    private Request premisesCategory;
}
