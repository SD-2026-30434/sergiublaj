package en.sd;

import en.sd.entity.ChefEntity;
import en.sd.entity.OrderEntity;
import en.sd.model.domain.Order;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;
import java.util.UUID;

@UtilityClass
public class TestFixtures {

    public ChefEntity chefEntity() {
        return chefEntity(UUID.randomUUID());
    }

    public ChefEntity chefEntity(final UUID id) {
        return ChefEntity.builder()
                .id(id)
                .name("Mario")
                .email("mario@example.com")
                .birthDate(ZonedDateTime.now().minusYears(30))
                .rating(4.5)
                .build();
    }

    public OrderEntity orderEntity(final UUID id, final ChefEntity chef) {
        return OrderEntity.builder()
                .id(id)
                .itemName("Pizza")
                .totalPrice(12.5)
                .orderedAt(ZonedDateTime.now())
                .chef(chef)
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
}
