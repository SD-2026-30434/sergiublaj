package en.sd.messaging.listener;

import en.sd.messaging.TestFixtures;
import en.sd.service.mail.OrderMailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCreatedListenerTest {

    @Mock
    private OrderMailService orderMailService;

    @InjectMocks
    private OrderCreatedListener orderCreatedListener;

    @Test
    void givenOrderCreatedEvent_whenOnOrderCreated_thenDelegatesToOrderMailServiceWithEventIds() {
        // given
        final var chefId = UUID.randomUUID();
        final var orderId = UUID.randomUUID();
        final var request = TestFixtures.orderCreatedEvent(chefId, orderId);
        when(orderMailService.sendOrderMail(chefId, orderId)).thenReturn(TestFixtures.successOrderMailResult());

        // when
        orderCreatedListener.onOrderCreated(request);

        // then
        verify(orderMailService).sendOrderMail(chefId, orderId);
        verifyNoMoreInteractions(orderMailService);
    }

    @Test
    void givenOrderMailServiceThrows_whenOnOrderCreated_thenExceptionPropagates() {
        // given
        final var request = TestFixtures.orderCreatedEvent();
        final var failure = new RuntimeException("downstream failure");
        when(orderMailService.sendOrderMail(any(), any())).thenThrow(failure);

        // when
        final var thrown = catchThrowable(() -> orderCreatedListener.onOrderCreated(request));

        // then
        assertThat(thrown).isSameAs(failure);
    }
}
