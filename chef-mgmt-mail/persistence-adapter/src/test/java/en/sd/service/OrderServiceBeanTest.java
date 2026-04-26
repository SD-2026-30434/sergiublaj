package en.sd.service;

import en.sd.entity.OrderEntity;
import en.sd.mapper.OrderMapper;
import en.sd.model.domain.Order;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceBeanTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceBean orderServiceBean;

    @Test
    void givenOrderEntityExists_whenGetById_thenReturnsMappedDomainOrder() {
        // given
        final var id = UUID.randomUUID();
        final var entity = new OrderEntity(id, "Pizza", 12.5, ZonedDateTime.now(), null);
        final var domain = new Order(id, "Pizza", 12.5, entity.getOrderedAt(), UUID.randomUUID());
        when(orderRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderMapper.toDomain(entity)).thenReturn(domain);

        // when
        final var result = orderServiceBean.getById(id);

        // then
        assertThat(result).isSameAs(domain);
        verify(orderRepository).findById(id);
        verify(orderMapper).toDomain(entity);
    }

    @Test
    void givenOrderEntityMissing_whenGetById_thenThrowsDataNotFoundException() {
        // given
        final var id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        // when
        final var thrown = catchThrowable(() -> orderServiceBean.getById(id));

        // then
        assertThat(thrown)
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining(id.toString());
        assertThat(((DataNotFoundException) thrown).getCode()).isEqualTo(ExceptionCode.ORDER_NOT_FOUND.getCode());
        verifyNoInteractions(orderMapper);
    }
}
