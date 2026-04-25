package en.sd.model.mail;

import java.util.UUID;

public record OrderMailResult(UUID id, String to, SendingStatus status) {
}
