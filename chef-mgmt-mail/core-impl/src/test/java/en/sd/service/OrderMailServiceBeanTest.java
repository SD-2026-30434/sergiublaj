package en.sd.service;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.model.mail.MailType;
import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailCreationService;
import en.sd.service.mail.MailSenderService;
import en.sd.service.persistence.ChefService;
import en.sd.service.persistence.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMailServiceBeanTest {

    private static final String ORDER_PLACED_SUBJECT = "You have a new order";

    @Mock
    private ChefService chefService;
    @Mock
    private OrderService orderService;
    @Mock
    private MailCreationService mailCreationService;
    @Mock
    private MailSenderService mailSenderService;

    @InjectMocks
    private OrderMailServiceBean orderMailServiceBean;

    @Test
    void givenChefAndOrderExist_whenSendOrderMail_thenReturnsSuccessResultAndDispatchesMail() {
        // given
        final var chefId = UUID.randomUUID();
        final var orderId = UUID.randomUUID();
        final var chef = new Chef(chefId, "Mario", "mario@example.com");
        final var order = new Order(orderId, "Pizza", 12.5, ZonedDateTime.now(), chefId);
        final var renderedHtml = "<html>order</html>";
        final var expected = new OrderMailResult(null, chef.email(), SendingStatus.SUCCESS);
        when(chefService.getById(chefId)).thenReturn(chef);
        when(orderService.getById(orderId)).thenReturn(order);
        when(mailCreationService.render(eq(MailType.ORDER_PLACED), any())).thenReturn(renderedHtml);
        when(mailSenderService.sendHtml(eq(chef.email()), eq(ORDER_PLACED_SUBJECT), eq(renderedHtml)))
                .thenReturn(SendingStatus.SUCCESS);

        // when
        final var result = orderMailServiceBean.sendOrderMail(chefId, orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
        verify(mailCreationService).render(MailType.ORDER_PLACED, Map.of("chef", chef, "order", order));
        verify(mailSenderService).sendHtml(chef.email(), ORDER_PLACED_SUBJECT, renderedHtml);
    }

    @Test
    void givenMailSenderReturnsFailure_whenSendOrderMail_thenResultStatusIsFailure() {
        // given
        final var chefId = UUID.randomUUID();
        final var orderId = UUID.randomUUID();
        final var chef = new Chef(chefId, "Mario", "mario@example.com");
        final var order = new Order(orderId, "Pizza", 12.5, ZonedDateTime.now(), chefId);
        final var expected = new OrderMailResult(null, chef.email(), SendingStatus.FAILURE);
        when(chefService.getById(chefId)).thenReturn(chef);
        when(orderService.getById(orderId)).thenReturn(order);
        when(mailCreationService.render(eq(MailType.ORDER_PLACED), any())).thenReturn("<html/>");
        when(mailSenderService.sendHtml(anyString(), anyString(), anyString()))
                .thenReturn(SendingStatus.FAILURE);

        // when
        final var result = orderMailServiceBean.sendOrderMail(chefId, orderId);

        // then
        assertThat(result.id()).isNotNull();
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    void givenChefDoesNotExist_whenSendOrderMail_thenThrowsDataNotFoundExceptionAndSkipsRestOfFlow() {
        // given
        final var chefId = UUID.randomUUID();
        final var orderId = UUID.randomUUID();
        when(chefService.getById(chefId))
                .thenThrow(new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId));

        // when
        final var thrown = catchThrowable(() -> orderMailServiceBean.sendOrderMail(chefId, orderId));

        // then
        assertThat(thrown)
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining(chefId.toString());
        assertThat(((DataNotFoundException) thrown).getCode()).isEqualTo(ExceptionCode.CHEF_NOT_FOUND.getCode());
        verifyNoInteractions(orderService, mailCreationService, mailSenderService);
    }

    @Test
    void givenOrderDoesNotExist_whenSendOrderMail_thenThrowsDataNotFoundExceptionAndDoesNotSendMail() {
        // given
        final var chefId = UUID.randomUUID();
        final var orderId = UUID.randomUUID();
        final var chef = new Chef(chefId, "Mario", "mario@example.com");
        when(chefService.getById(chefId)).thenReturn(chef);
        when(orderService.getById(orderId))
                .thenThrow(new DataNotFoundException(ExceptionCode.ORDER_NOT_FOUND, orderId));

        // when
        final var thrown = catchThrowable(() -> orderMailServiceBean.sendOrderMail(chefId, orderId));

        // then
        assertThat(thrown)
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining(orderId.toString());
        assertThat(((DataNotFoundException) thrown).getCode()).isEqualTo(ExceptionCode.ORDER_NOT_FOUND.getCode());
        verifyNoInteractions(mailCreationService, mailSenderService);
    }
}
