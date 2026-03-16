package en.sd.chefmgmt.dto.chef;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ChefResponseDTO(
        UUID id,
        String name,
        String email,
        ZonedDateTime birthDate,
        double numberOfStars
) { }