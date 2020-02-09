package af.gov.anar.ebreshna.helpdesk.model;

import af.gov.anar.ebreshna.common.model.BaseEntity;
import af.gov.anar.ebreshna.common.province.Province;
import af.gov.anar.ebreshna.helpdesk.enumeration.ModuleType;
import af.gov.anar.lib.workflow.model.Workflow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "complaint")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Audited
public class Complaint  extends BaseEntity {

    private String complainantName;

    private String email;

    @OneToOne(targetEntity = Workflow.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "workflow_id")
    private Workflow workflow;

    @OneToOne(targetEntity = Province.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "province_id")
    private Province province;

    @OneToOne(targetEntity = Province.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true, name = "complaint_type_id")
    private ComplaintType complaintType;

    private String junction;

    private ModuleType moduleType;

    private String mobileNumber;

    private String attachmentPath;

    private String content;

    @OneToMany(mappedBy = "complaint", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Comment> comments;


    @OneToMany(mappedBy = "complaint", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<ComplaintHistory> complaintHistories;

}
