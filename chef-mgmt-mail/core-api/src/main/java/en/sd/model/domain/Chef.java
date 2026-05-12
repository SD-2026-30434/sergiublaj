package en.sd.model.domain;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Chef(
        UUID id,
        String name,
        String email
) { }
