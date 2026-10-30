package petTopia.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.user.request.ChangePasswordRequest;
import petTopia.service.user.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class PasswordController {
    private final MemberService memberService;

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        memberService.changePassword(request);
        return ResponseEntity.ok().build();
    }
} 