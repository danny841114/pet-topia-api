package petTopia.dto.vendor_admin;

import lombok.*;
import petTopia.model.vendor.VendorTag;
import petTopia.model.vendor.Vendor;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCertificationDto {
    private Integer id;
    private Vendor vendor;
    private String certificationStatus;
    private String reason;
    private Date requestDate;
    private Date approvedDate;
    private TagDto tag;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagDto{
        private Integer id;
        private VendorTag tag;
        private boolean meetsStandard;
    }
}
