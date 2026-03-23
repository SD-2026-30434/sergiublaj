package en.sd.chefmgmt.dto.chef;

import en.sd.chefmgmt.dto.order.OrderResponseDTO;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ChefResponseDTO(
        UUID id,
        String name,
        String email,
        ZonedDateTime birthDate,
        double numberOfStars,
        List<OrderResponseDTO> orders
) { }