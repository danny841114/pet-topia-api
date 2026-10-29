package petTopia.dto.user.response;

public record VerifyCodeResponse(String message, String email, boolean verified) {
}
