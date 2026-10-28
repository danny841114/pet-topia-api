package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import petTopia.dto.user.request.RegisterRequest;
import petTopia.dto.user.request.VerifyCodeRequest;
import petTopia.model.user.User;
import petTopia.service.user.EmailService;
import petTopia.service.user.RegistrationService;
import petTopia.service.user.VerificationCodeService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class MemberRegisterController {
    private final RegistrationService registrationService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String registrationCode = registrationService.register(request);
        emailService.sendVerificationEmail(request.getEmail(), registrationCode);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email is not valid");

        String code = verificationCodeService.generateAndSaveCode(email);
        emailService.sendVerificationCode(email, code);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        String email = request.email();
        String code = request.code();

        if (email == null || code == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "電子郵件和驗證碼不能為空"));
        }

        log.info("驗證驗證碼 - 電子郵件: {}", email);

        // 第一步：檢查內存中是否有驗證碼
        Map<String, Object> codeData = verificationCodes.get(email);
        if (codeData != null) {
            String storedCode = (String) codeData.get("code");
            long timestamp = (long) codeData.get("timestamp");

            // 檢查驗證碼是否過期（5分鐘）
            if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
                verificationCodes.remove(email);
                log.warn("驗證失敗 - 內存中驗證碼已過期 - 電子郵件: {}", email);
                // 不立即返回錯誤，繼續檢查數據庫中的驗證碼
            } else if (code.equals(storedCode)) {
                // 如果內存中的驗證碼匹配，則嘗試同時更新數據庫中的驗證狀態
                try {
                    // 嘗試在數據庫中查找用戶並更新驗證狀態
                    User user = registrationService.findByEmail(email);
                    if (user != null) {
                        // 更新數據庫中的驗證狀態
                        user.setEmailVerified(true);
                        registrationService.updateUser(user);
                        log.info("驗證成功(內存驗證碼) - 已更新數據庫狀態 - 電子郵件: {}", email);
                    }
                } catch (Exception e) {
                    log.warn("內存驗證成功但更新數據庫失敗 - 電子郵件: {}", email, e);
                    // 即使數據庫更新失敗，仍然可以繼續，因為內存驗證已成功
                }

                // 清除內存中的驗證碼
                verificationCodes.remove(email);

                log.info("驗證成功(內存驗證碼) - 電子郵件: {}", email);
                return ResponseEntity.ok(Map.of(
                        "message", "驗證成功",
                        "email", email,
                        "verified", true
                ));
            }
        }

        // 第二步：如果內存中沒有驗證碼或驗證失敗，則檢查數據庫
        try {
            log.info("嘗試從數據庫驗證 - 電子郵件: {}, 驗證碼: {}", email, code);
            boolean isVerified = registrationService.verifyEmail(email, code);

            if (isVerified) {
                log.info("驗證成功(數據庫驗證碼) - 電子郵件: {}", email);
                return ResponseEntity.ok(Map.of(
                        "message", "驗證成功",
                        "email", email,
                        "verified", true
                ));
            } else {
                log.warn("驗證失敗(數據庫驗證碼) - 電子郵件: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "驗證碼錯誤或已過期"));
            }
        } catch (Exception e) {
            log.error("驗證過程發生數據庫錯誤 - 電子郵件: {}", email, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "驗證過程發生錯誤: " + e.getMessage()));
        }
    }
}