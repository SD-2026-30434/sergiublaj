package en.sd.chefmgmt.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final RabbitTemplate rabbitTemplate;

    @Value("${chef-mgmt.messaging.order-mail.queue}")
    private String orderMailQueueName;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[ORDER_CREATED_EVENT] Publishing: chef={} order={}", event.chefId(), event.orderId());

        rabbitTemplate.convertAndSend(orderMailQueueName, event);
    }
}
