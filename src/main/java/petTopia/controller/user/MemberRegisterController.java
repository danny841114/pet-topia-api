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
import petTopia.dto.user.response.VerifyCodeResponse;
import petTopia.service.user.EmailService;
import petTopia.service.user.RegistrationService;
import petTopia.service.user.VerificationCodeService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class MemberRegisterController {
    private final RegistrationService registrationService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

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
    public ResponseEntity<VerifyCodeResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        String email = request.email();
        String code = request.code();
        if (email == null || code == null) throw new IllegalArgumentException("Email or code is not valid");

        boolean isVerified = verificationCodeService.verifyCode(email, code);
        if (isVerified) {
            registrationService.updateUserEmailVerified(email);
            return ResponseEntity.ok(new VerifyCodeResponse("驗證成功", email, true));
        } else {
            return ResponseEntity.ok(new VerifyCodeResponse("驗證失敗", email, false));
        }
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<VerifyCodeResponse> verifyRegistration(@RequestBody VerifyCodeRequest request) {
        String email = request.email();
        String code = request.code();
        if (email == null || code == null) throw new IllegalArgumentException("Email or code is not valid");

        boolean isVerified = registrationService.verifyEmail(email, code);
        if (isVerified) {
            return ResponseEntity.ok(new VerifyCodeResponse("驗證成功", email, true));
        } else {
            return ResponseEntity.ok(new VerifyCodeResponse("驗證失敗", email, false));
        }
    }
}