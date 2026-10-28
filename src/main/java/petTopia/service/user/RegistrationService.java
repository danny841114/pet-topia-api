package petTopia.service.user;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.user.request.RegisterRequest;
import petTopia.dto.user.response.CheckEmailResponse;
import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final UserRepository usersRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (email == null || email.trim().isEmpty()) throw new InvalidParameterException("Email is invalid");
        if (password == null || password.trim().isEmpty()) throw new InvalidParameterException("Password is invalid");
        if (!password.equals(confirmPassword)) throw new InvalidParameterException("Confirm password is invalid");

        String trimmedEmail = email.toLowerCase().trim();
        if (usersRepository.findByEmailAndUserRole(trimmedEmail, User.UserRole.MEMBER).isPresent())
            throw new BadCredentialsException("This email has been registered");

        User user = new User();
        user.setEmail(trimmedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(User.UserRole.MEMBER);
        user.setProvider(User.Provider.LOCAL);

        // 生成6位數驗證碼
        String code = String.format("%06d", new Random().nextInt(1000000));
        user.setVerificationToken(code);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));

        usersRepository.save(user);
        log.info("User has been added, userId: {}", user.getId());

        Member member = new Member();
        member.setUser(user);
        member.setStatus(false);
        member.setUpdatedDate(LocalDateTime.now());

        memberRepository.save(member);
        log.info("Member has been added, memberId: {}", member.getId());

        return code;
    }

    @Transactional
    public boolean verifyEmail(String email, String code) {
        Optional<User> userOptional = usersRepository.findByEmailAndVerificationToken(email, code);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!user.isEmailVerified() && LocalDateTime.now().isBefore(user.getTokenExpiry())) {
                // 更新用戶驗證狀態
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setTokenExpiry(null);
                usersRepository.save(user);

                // 更新會員狀態
                memberRepository.findByUserId(user.getId()).ifPresent(member -> {
                    member.setStatus(true);
                    memberRepository.save(member);
                    log.info("會員驗證成功，userId: {}", user.getId());
                });

                return true;
            }
        }

        return false;
    }

    public User findByEmail(String email) {
        // 查找任何類型的帳號（會員、商家、本地、Google）
        String trimmedEmail = email.toLowerCase().trim();
        return usersRepository.findByEmailAndUserRole(trimmedEmail, User.UserRole.MEMBER)
                .orElse(null);
    }

    @Transactional
    public void updateUser(User user) {
        // 保存用戶信息
        log.info("更新用戶信息，userId: {}", user.getId());
        usersRepository.save(user);
    }

    public CheckEmailResponse checkEmailIsRegistered(String email) {
        log.info("檢查 email 是否已存在 - email: {}", email);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("檢查 email 失敗 - email 為空");
        }

        User existingUser = this.findByEmail(email);
        if (existingUser != null) {
            String message;
            if (existingUser.getProvider() == User.Provider.GOOGLE) {
                message = "此 email 已使用 Google 帳號登入過，請點擊「使用 Google 登入」按鈕";
            } else if (existingUser.getUserRole() == User.UserRole.VENDOR) {
                message = "此 email 已註冊為商家帳號，請使用其他 email 註冊會員";
            } else {
                message = "此 email 已註冊為會員帳號，請直接登入";
            }

            log.info("Email 已存在 - email: {}, 用戶類型: {}", email, existingUser.getUserRole());

            return CheckEmailResponse.builder()
                    .exists(true)
                    .message(message)
                    .userRole(existingUser.getUserRole())
                    .provider(existingUser.getProvider())
                    .build();
        } else {
            log.info("Email 可用 - email: {}", email);

            return CheckEmailResponse.builder()
                    .exists(true)
                    .message("此 email 可用於註冊")
                    .build();
        }
    }
}
