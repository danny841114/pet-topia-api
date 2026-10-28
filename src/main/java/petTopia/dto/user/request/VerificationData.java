package petTopia.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationData {
    private String code;
    private long timestamp;

    public boolean isExpired(long expiredDurationMills) {
        return System.currentTimeMillis() - this.timestamp > expiredDurationMills;
    }
}
