package en.sd.messaging.listener;

import en.sd.messaging.event.OrderCreatedEvent;
import en.sd.service.mail.OrderMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final OrderMailService orderMailService;

    @RabbitListener(queuesToDeclare = @Queue(name = "${chef-mgmt.messaging.order-mail.queue}", durable = "true"))
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[ORDER_CREATED_EVENT] Received: chef={} order={}", event.chefId(), event.orderId());

        orderMailService.sendOrderMail(event.chefId(), event.orderId());
    }
}
