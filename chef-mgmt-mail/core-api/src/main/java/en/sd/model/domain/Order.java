package en.sd.model.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

public record Order(
        UUID id,
        String itemName,
        Double totalPrice,
        ZonedDateTime orderedAt,
        UUID chefId
) {
}
