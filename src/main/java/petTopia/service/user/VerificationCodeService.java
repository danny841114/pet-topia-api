package petTopia.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import petTopia.dto.user.request.VerificationData;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class VerificationCodeService {
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;

    private final Map<String, VerificationData> storage = new ConcurrentHashMap<>();

    public String generateAndSaveCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        storage.put(email, new VerificationData(code, System.currentTimeMillis()));
        log.info("Verification code has been generated, email: {}, code: {}", email, code);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        VerificationData data = storage.get(email);

        if (data == null) return false;

        if (data.isExpired(EXPIRATION_TIME)) {
            storage.remove(email);
            return false;
        }

        if (code.equals(data.getCode())) {
            storage.remove(email);
            return true;
        }

        return false;
    }
}
