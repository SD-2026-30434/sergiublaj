package en.sd.model.domain;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record Order(
        UUID id,
        String itemName,
        Double totalPrice,
        ZonedDateTime orderedAt,
        UUID chefId
) { }
