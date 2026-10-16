package petTopia.dto.vendor_admin;

import lombok.*;
import petTopia.model.vendor.VendorTag;
import petTopia.model.vendor.VendorTagCertification;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCertificationDto {
    private Integer id;
    private Integer vendorId;
    private VendorTag tag;
    private String status;
    private String reason;
    private Date requestDate;
    private Date approvedDate;
    private boolean meetsStandard;

    public static TagCertificationDto fromEntity(VendorTagCertification certification) {
        if (certification == null) return null;

        Integer vendorId = certification.getVendor() != null ? certification.getVendor().getId() : null;

        return TagCertificationDto.builder()
                .id(certification.getId())
                .vendorId(vendorId)
                .tag(certification.getTag())
                .status(certification.getStatus())
                .reason(certification.getReason())
                .requestDate(certification.getRequestDate())
                .approvedDate(certification.getApprovedDate())
                .meetsStandard(certification.getMeetsStandard())
                .build();
    }
}
