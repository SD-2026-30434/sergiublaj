package en.sd;

import en.sd.messaging.event.OrderCreatedEvent;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TestFixtures {

    public OrderCreatedEvent orderCreatedEvent(final UUID chefId, final UUID orderId) {
        return OrderCreatedEvent.builder()
                .chefId(chefId)
                .orderId(orderId)
                .build();
    }
}
