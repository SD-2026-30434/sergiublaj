package en.sd.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import en.sd.IntegrationTestApplication;
import en.sd.messaging.event.OrderCreatedEvent;
import en.sd.messaging.listener.OrderCreatedListener;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ActiveProfiles("test")
@SpringBootTest(classes = IntegrationTestApplication.class)
class OrderFlowIntegrationTest {

    private static final UUID SEEDED_CHEF_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String SEEDED_CHEF_NAME = "Mario Rossi";
    private static final String SEEDED_CHEF_EMAIL = "mario@example.com";
    private static final UUID SEEDED_ORDER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String SEEDED_ORDER_ITEM = "Pizza Margherita";
    private static final Double SEEDED_ORDER_PRICE = 12.5;
    private static final UUID SEEDED_CHEF_WITHOUT_ORDER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @RegisterExtension
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    private OrderCreatedListener orderCreatedListener;

    @BeforeEach
    void resetMailbox() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void givenSeededChefAndOrder_whenListenerReceivesEvent_thenMailIsSentToChef() throws Exception {
        // given
        final var event = new OrderCreatedEvent(SEEDED_CHEF_ID, SEEDED_ORDER_ID);

        // when
        orderCreatedListener.onOrderCreated(event);

        // then
        final var messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        final var message = messages[0];
        assertThat(message.getAllRecipients()).hasSize(1);
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(SEEDED_CHEF_EMAIL);
        assertThat(message.getFrom()[0].toString()).isEqualTo("noreply@chefmgmt.com");
        assertThat(message.getSubject()).isEqualTo("You have a new order");
        final var body = GreenMailUtil.getBody(message);
        assertThat(body)
                .contains(SEEDED_CHEF_NAME)
                .contains(SEEDED_CHEF_EMAIL)
                .contains(SEEDED_ORDER_ITEM)
                .contains(SEEDED_ORDER_PRICE.toString());
    }

    @Test
    void givenChefMissing_whenListenerReceivesEvent_thenThrowsChefNotFoundAndSendsNoMail() {
        // given
        final var event = new OrderCreatedEvent(UUID.randomUUID(), UUID.randomUUID());

        // when
        final var thrown = catchThrowable(() -> orderCreatedListener.onOrderCreated(event));

        // then
        assertThat(thrown)
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining(event.chefId().toString());
        assertThat(((DataNotFoundException) thrown).getCode()).isEqualTo(ExceptionCode.CHEF_NOT_FOUND.getCode());
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void givenChefExistsButOrderMissing_whenListenerReceivesEvent_thenThrowsOrderNotFoundAndSendsNoMail() {
        // given
        final var event = new OrderCreatedEvent(SEEDED_CHEF_WITHOUT_ORDER_ID, UUID.randomUUID());

        // when
        final var thrown = catchThrowable(() -> orderCreatedListener.onOrderCreated(event));

        // then
        assertThat(thrown)
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining(event.orderId().toString());
        assertThat(((DataNotFoundException) thrown).getCode()).isEqualTo(ExceptionCode.ORDER_NOT_FOUND.getCode());
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }
}
