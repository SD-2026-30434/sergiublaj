package en.sd.messaging.event;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID chefId,
        UUID orderId
) { }
