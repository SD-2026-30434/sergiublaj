package en.sd.chefmgmt.dto.user;

import en.sd.chefmgmt.model.UserRole;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        UserRole role,
        UUID chefId,
        String chefName,
        ZonedDateTime chefBirthDate,
        double chefRating
) { }
