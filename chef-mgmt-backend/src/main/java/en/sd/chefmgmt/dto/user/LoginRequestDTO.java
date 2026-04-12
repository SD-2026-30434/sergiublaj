package en.sd.chefmgmt.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String email,
        @NotBlank String password
) { }
