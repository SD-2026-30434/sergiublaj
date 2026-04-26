package en.sd.chefmgmt.security;

import en.sd.chefmgmt.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final UserRole role;
    private final UUID chefId;

    @NonNull
    @Override
    public String getUsername() {
        return email;
    }

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
