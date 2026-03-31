package en.sd.chefmgmt.service.user;

import en.sd.chefmgmt.dto.user.UserResponseDTO;
import en.sd.chefmgmt.model.ChefEntity;
import en.sd.chefmgmt.model.UserEntity;
import en.sd.chefmgmt.repository.user.UserRepository;
import en.sd.chefmgmt.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceBean implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getCurrentUser() {
        UserDetailsImpl principal = (UserDetailsImpl) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                .getPrincipal();

        UserEntity user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow();

        ChefEntity chef = user.getChef();

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                chef != null ? chef.getId() : null,
                chef != null ? chef.getName() : null,
                chef != null ? chef.getBirthDate() : null,
                chef != null ? chef.getRating() : 0
        );
    }
}
