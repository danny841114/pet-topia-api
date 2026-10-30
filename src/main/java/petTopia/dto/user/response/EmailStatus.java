package petTopia.dto.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailStatus {
    private String email;
    private String provider;
    private Boolean canSetupLocalPassword;
}
