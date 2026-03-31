package en.sd.chefmgmt.controller.user;

import en.sd.chefmgmt.dto.user.UserResponseDTO;
import en.sd.chefmgmt.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/v1")
@Slf4j
@RequiredArgsConstructor
public class UserControllerBean implements UserController {

    private final UserService userService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO getCurrentUser() {
        log.info("[USER] Fetching current user info");

        return userService.getCurrentUser();
    }
}
