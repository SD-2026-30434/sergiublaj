package en.sd.model.mail;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChefWelcomeMailRequestDTO(@NotNull UUID chefId) { }
