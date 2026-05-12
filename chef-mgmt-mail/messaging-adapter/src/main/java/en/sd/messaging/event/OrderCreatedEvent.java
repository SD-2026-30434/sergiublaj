package en.sd.messaging.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCreatedEvent(
        UUID chefId,
        UUID orderId
) { }
