package en.sd.chefmgmt.event;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID chefId,
        UUID orderId
) { }
