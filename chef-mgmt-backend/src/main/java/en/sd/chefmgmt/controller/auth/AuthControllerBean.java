package en.sd.chefmgmt.controller.auth;

import en.sd.chefmgmt.dto.user.LoginRequestDTO;
import en.sd.chefmgmt.security.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthControllerBean implements AuthController {

    private final CookieService cookieService;

    @Override
    public void login(LoginRequestDTO loginRequestDTO) {
        // Handled by LoginAuthenticationFilter — this method exists only for Swagger documentation
    }

    @Override
    public void logout(HttpServletResponse response) {
        cookieService.clearAccessTokenCookie(response);
    }
}
