package en.sd.service;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;
import en.sd.model.mail.MailType;
import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailCreationService;
import en.sd.service.mail.MailSenderService;
import en.sd.service.mail.OrderMailService;
import en.sd.service.persistence.ChefService;
import en.sd.service.persistence.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMailServiceBean implements OrderMailService {

    private static final String ORDER_PLACED_SUBJECT = "You have a new order";

    private final ChefService chefService;
    private final OrderService orderService;
    private final MailCreationService mailCreationService;
    private final MailSenderService mailSenderService;

    @Override
    public OrderMailResult sendOrderMail(UUID chefId, UUID orderId) {
        Chef chef = chefService.getById(chefId);
        Order order = orderService.getById(orderId);
        String htmlBody = mailCreationService.render(MailType.ORDER_PLACED, Map.of("chef", chef, "order", order));
        SendingStatus status = mailSenderService.sendHtml(chef.email(), ORDER_PLACED_SUBJECT, htmlBody);
        UUID correlationId = UUID.randomUUID();
        log.info("Order mail dispatched: id={} order={} to={} status={}", correlationId, orderId, chef.email(), status);

        return new OrderMailResult(correlationId, chef.email(), status);
    }
}
