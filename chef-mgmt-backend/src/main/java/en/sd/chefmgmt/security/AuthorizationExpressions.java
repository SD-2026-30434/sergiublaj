package en.sd.chefmgmt.security;

import en.sd.chefmgmt.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationExpressions {

    private final OrderRepository orderRepository;

    public boolean isOwnChef(UUID chefId) {
        if (chefId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null &&
                authentication.getPrincipal() instanceof UserDetailsImpl principal &&
                principal.getChefId() != null &&
                principal.getChefId().equals(chefId);
    }

    public boolean isOrderOfChef(UUID orderId, UUID chefId) {
        return orderId != null &&
                chefId != null &&
                orderRepository.existsByIdAndChefId(orderId, chefId);
    }
}
