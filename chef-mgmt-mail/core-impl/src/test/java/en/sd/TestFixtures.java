package en.sd;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;
import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendingStatus;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;
import java.util.UUID;

@UtilityClass
public class TestFixtures {

    public Chef chef(final UUID id) {
        return Chef.builder()
                .id(id)
                .name("Mario")
                .email("mario@example.com")
                .build();
    }

    public Order order(final UUID id, final UUID chefId) {
        return Order.builder()
                .id(id)
                .itemName("Pizza")
                .totalPrice(12.5)
                .orderedAt(ZonedDateTime.now())
                .chefId(chefId)
                .build();
    }

    public OrderMailResult orderMailResult(final String email, final SendingStatus status) {
        return OrderMailResult.builder()
                .to(email)
                .status(status)
                .build();
    }
}
