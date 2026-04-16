package en.sd.chefmgmt.dto.order;

import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        String itemName,
        Double totalPrice,
        ZonedDateTime orderedAt,
        UUID chefId,
        String chefName
) { }
