package petTopia.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.user.request.LoginRequest;
import petTopia.dto.user.response.LoginResponse;
import petTopia.dto.user.response.LoginStatus;
import petTopia.service.user.MemberLoginService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class MemberLoginController {
    private final MemberLoginService memberLoginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResult = memberLoginService.login(request);
        return ResponseEntity.ok(loginResult);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<LoginStatus> getLoginStatus(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        LoginStatus loginStatus = memberLoginService.getLoginStatus(authHeader);
        return ResponseEntity.ok(loginStatus);
    }
}
