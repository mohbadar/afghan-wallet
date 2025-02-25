package af.asr.customer.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import af.asr.infrastructure.revision.AuditEnabledEntity;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "maat_customers", schema = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Audited
public class CustomerEntity extends AuditEnabledEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "a_type")
    private String type;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "given_name")
    private String givenName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "surname")
    private String surname;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "is_member", nullable = false)
    private Boolean member;
    @Column(name = "account_beneficiary")
    private String accountBeneficiary;
    @Column(name = "reference_customer")
    private String referenceCustomer;
    @Column(name = "assigned_office")
    private String assignedOffice;
    @Column(name = "assigned_employee")
    private String assignedEmployee;
    @Column(name = "current_state")
    private String currentState;
    @Column(name = "application_date")
//    @Convert(converter = LocalDateConverter.class)
    private LocalDate applicationDate;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private AddressEntity address;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_on")
//    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    @Column(name = "last_modified_on")
//    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastModifiedOn;

}