package en.sd.chefmgmt.model.mail;

import java.util.UUID;

public record ChefWelcomeMailResponseDTO(
        UUID id,
        String to,
        String status
) { }
