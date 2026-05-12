package en.sd.mapper;

import en.sd.TestFixtures;
import en.sd.entity.OrderEntity;
import en.sd.model.domain.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void givenOrderEntityWithChef_whenToDomain_thenMapsAllFieldsAndChefId() {
        // given
        final var chefId = UUID.randomUUID();
        final var orderId = UUID.randomUUID();
        final var chef = TestFixtures.chefEntity(chefId);
        final var request = TestFixtures.orderEntity(orderId, chef);
        final var expected = new Order(
                orderId, request.getItemName(), request.getTotalPrice(), request.getOrderedAt(), chefId);

        // when
        final var result = orderMapper.toDomain(request);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void givenNullEntity_whenToDomain_thenReturnsNull() {
        // given
        final OrderEntity request = null;

        // when
        final var result = orderMapper.toDomain(request);

        // then
        assertThat(result).isNull();
    }
}
