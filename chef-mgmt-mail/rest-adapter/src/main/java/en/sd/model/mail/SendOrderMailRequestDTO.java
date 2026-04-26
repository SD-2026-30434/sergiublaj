package en.sd.model.mail;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendOrderMailRequestDTO(
        @NotNull UUID chefId,
        @NotNull UUID orderId
) { }
