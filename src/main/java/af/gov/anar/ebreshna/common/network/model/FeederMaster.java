package af.gov.anar.ebreshna.common.network.model;


import af.gov.anar.ebreshna.common.base.BaseEntity;
import af.gov.anar.ebreshna.common.network.enumeration.*;
import af.gov.anar.ebreshna.infrastructure.util.Schema;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "network_feeder_master", schema = Schema.CORE_SCHEMA)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Audited
public class FeederMaster extends BaseEntity {

    @ManyToOne(targetEntity = StationType.class)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private StationType stationType;

    @ManyToOne(targetEntity = StationMaster.class)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private StationMaster stationMaster;

    @Column
    private String feederName;

    @Column
    private FeederPowerSupply feederPowerSupply;

    @Column
    private FeederType feederType;

    @Column
    private String feederCode;

    @Column
    private String feederAt;

    @Column
    private FeederOrganization organization;

    @Column
    private String feederLocation;

    @Column
    private FeederCategory feederCategory;

    @Column
    private FeederLoadShedCatogory feederLoadShedCatogory;

    @Column
    private String maxLoad;

    @ManyToOne(targetEntity = VoltageLevel.class)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private VoltageLevel voltageLevel;

    @Column
    private String ain;

}
