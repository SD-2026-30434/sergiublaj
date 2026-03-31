package en.sd.chefmgmt.controller.auth;

import en.sd.chefmgmt.dto.user.LoginRequestDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
public class AuthControllerBean implements AuthController {

    @Override
    public void login(LoginRequestDTO loginRequestDTO) {
        // Handled by LoginAuthenticationFilter — this method exists only for Swagger documentation
    }
}
