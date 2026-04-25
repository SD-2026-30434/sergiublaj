package en.sd.chefmgmt.service.mailing.client;

import java.util.UUID;

public record SendOrderMailRequestDTO(UUID chefId, UUID orderId) {
}
