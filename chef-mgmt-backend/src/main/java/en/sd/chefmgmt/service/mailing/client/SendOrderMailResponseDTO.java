package en.sd.chefmgmt.service.mailing.client;

import java.util.UUID;

public record SendOrderMailResponseDTO(UUID id, String to, String status) {
}
