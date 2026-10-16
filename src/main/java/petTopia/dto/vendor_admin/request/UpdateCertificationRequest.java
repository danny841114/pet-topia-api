package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCertificationRequest {
    private String status;
    private String reason;
}
