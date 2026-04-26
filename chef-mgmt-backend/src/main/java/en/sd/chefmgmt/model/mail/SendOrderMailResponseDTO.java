package en.sd.chefmgmt.model.mail;

import java.util.UUID;

public record SendOrderMailResponseDTO(UUID id, String to, String status) {
}
