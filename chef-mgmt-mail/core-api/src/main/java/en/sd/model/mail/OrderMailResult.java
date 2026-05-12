package en.sd.model.mail;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderMailResult(
        UUID id,
        String to,
        SendingStatus status
) { }
