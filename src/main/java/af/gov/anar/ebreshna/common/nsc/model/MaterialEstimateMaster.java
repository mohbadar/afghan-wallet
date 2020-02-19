package af.gov.anar.ebreshna.common.nsc.model;

import af.gov.anar.ebreshna.common.base.BaseEntity;
import af.gov.anar.ebreshna.common.nsc.enumeration.ItemHead;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "nsc_material_estimate_master")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Audited
public class MaterialEstimateMaster extends BaseEntity {

    @Column
    private ItemHead itemHead;


}
