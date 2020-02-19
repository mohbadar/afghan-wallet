package af.gov.anar.ebreshna.customerservice.model;

import af.gov.anar.ebreshna.common.base.BaseEntity;
import af.gov.anar.ebreshna.common.base.workflowdata.WorkflowTransitionData;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

@Entity
@Table(name = "customerservice_complaint_modifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Audited
public class ComplaintModification extends BaseEntity {

    @ManyToOne(targetEntity = Complaint.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "complaint_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Complaint complaint;

    @Column
    private String action;

    @Column
    private String oldContent;

    @Column
    private String currentContent;


}
