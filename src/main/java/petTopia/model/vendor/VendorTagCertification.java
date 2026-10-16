package petTopia.model.vendor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "vendor_tag_certification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorTagCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private VendorTag tag;

    @Column(name = "status", nullable = false, columnDefinition = "NVARCHAR(50) DEFAULT '申請中'")
    private String status;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "request_date", updatable = false)
    private Date requestDate = new Date();

    @Column(name = "approved_date")
    private Date approvedDate;

    @Column(name = "meets_standard", nullable = false)
    private Boolean meetsStandard = false;
}
