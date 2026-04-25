package en.sd.model.mail;

import java.util.UUID;

public record SendOrderMailResponseDTO(UUID id, String to, String status) {
}
