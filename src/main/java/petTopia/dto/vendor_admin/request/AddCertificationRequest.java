package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCertificationRequest {
    private Integer vendorId;
    private Integer tagId;
}
