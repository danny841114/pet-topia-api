package petTopia.dto.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String message;
    private String token;
    private Integer adminId;
    private String email;
    private String role;
    private String provider;
    private Integer userId;
    private String name;
    private Boolean success;
    private String error;
    private Boolean isAuthenticated;
    private Boolean isLocalEnabled;
}
