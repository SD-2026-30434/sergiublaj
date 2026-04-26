package en.sd.chefmgmt.model.mail;

import java.util.UUID;

public record SendOrderMailRequestDTO(UUID chefId, UUID orderId) {
}
