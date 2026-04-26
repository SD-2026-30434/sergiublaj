package en.sd.mapper;

import en.sd.entity.ChefEntity;
import en.sd.entity.OrderEntity;
import en.sd.model.domain.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void givenOrderEntityWithChef_whenToDomain_thenMapsAllFieldsAndChefId() {
        // given
        final var chefId = UUID.randomUUID();
        final var chef = new ChefEntity(chefId, "Mario", "mario@example.com", ZonedDateTime.now().minusYears(30), 4.5);
        final var orderId = UUID.randomUUID();
        final var orderedAt = ZonedDateTime.now();
        final var entity = new OrderEntity(orderId, "Pizza", 12.5, orderedAt, chef);
        final var expected = new Order(orderId, "Pizza", 12.5, orderedAt, chefId);

        // when
        final var domain = orderMapper.toDomain(entity);

        // then
        assertThat(domain).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void givenNullEntity_whenToDomain_thenReturnsNull() {
        // given
        final OrderEntity entity = null;

        // when
        final var domain = orderMapper.toDomain(entity);

        // then
        assertThat(domain).isNull();
    }
}
