package petTopia.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.response.LoginResponse;
import petTopia.dto.user.response.LoginStatus;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;
import petTopia.util.StringHelper;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberLoginService {
    private final UserRepository usersRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    // 前端應用的URL
    // TODO: move to application.properties
    private static final String FRONTEND_URL = "http://localhost:5173";

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (email == null || password == null) {
            log.warn("Email or password is null");
            throw new IllegalArgumentException("Email or password is null");
        }

        User user = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER)
                .orElseThrow(() -> new BadCredentialsException("User not found with '%s'".formatted(email)));
        Integer userId = user.getId();

        if (user.getProvider() != User.Provider.LOCAL && !user.getLocalEnabled()) {
            log.warn("This user is not allowed to login by password, userId: {}, provider: {}", userId, user.getProvider().name());
            throw new BadCredentialsException("This user is not allowed to login by password");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed with wrong password, userId: {}", userId);
            throw new BadCredentialsException("Login failed with wrong password");
        }

        if (!user.isEmailVerified()) {
            log.warn("Login failed with email not verified, userId: {}", userId);
            throw new BadCredentialsException("Login failed with email not verified, userId: %s".formatted(userId));
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(email, userId, user.getUserRole().toString());

        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            Member newMember = new Member();

            newMember.setId(userId);
            newMember.setUser(user);
            newMember.setName(email.split("@")[0]);
            newMember.setStatus(false);
            newMember.setUpdatedDate(LocalDateTime.now());

            memberRepository.save(newMember);
        }

        String displayName = member != null ? member.getName() : email.split("@")[0];

        return LoginResponse.builder()
                .token(token)
                .userId(userId)
                .email(email)
                .name(displayName)
                .role(user.getProvider().name())
                .provider(user.getProvider().name())
                .build();
    }

    public RedirectView oauth2Callback(String email) {
        try {
            User user = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER).orElse(null);

            String token = user != null
                    ? jwtUtil.generateToken(email, user.getId(), user.getUserRole().toString())
                    : null;

            boolean isNewUser = (user == null);

            StringBuilder redirectUrl = new StringBuilder(FRONTEND_URL);
            redirectUrl.append("/login?oauth2Success=true");
            redirectUrl.append("&token=").append(token);
            redirectUrl.append("&userId=").append(isNewUser ? null : user.getId());
            redirectUrl.append("&email=").append(email);
            redirectUrl.append("&role=").append(isNewUser ? null : user.getUserRole().toString());
            if (isNewUser) redirectUrl.append("&newUser=true");

            return new RedirectView(redirectUrl.toString());
        } catch (Exception e) {
            // 處理錯誤情況，重定向到帶有錯誤信息的登入頁面
            return new RedirectView(FRONTEND_URL + "/login?error=true&message=" + e.getMessage());
        }
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER).orElse(null);
    }

    public User findByEmailIfAbsentThrowException(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public LoginStatus getLoginStatus(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Header authorization is invalid");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        if (!jwtUtil.validateToken(token, email)) throw new BadCredentialsException("Token is invalid");

        Integer userId = jwtUtil.extractUserId(token);
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        User user = usersRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        String displayName = member.getName();

        return LoginStatus.builder()
                .isLoggedIn(true)
                .userId(userId)
                .email(email)
                .name(displayName)
                .memberName(displayName)
                .role(jwtUtil.extractUserRole(token))
                .provider(user.getProvider().toString())
                .build();
    }
}