package en.sd.service;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;
import en.sd.service.mail.MailCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailCreationServiceBean implements MailCreationService {

    private static final String ORDER_PLACED_TEMPLATE = "order-placed";
    private static final String CHEF_VAR = "chef";
    private static final String ORDER_VAR = "order";

    private final SpringTemplateEngine templateEngine;

    @Override
    public String renderOrderPlaced(Chef chef, Order order) {
        Context context = new Context();
        context.setVariable(CHEF_VAR, chef);
        context.setVariable(ORDER_VAR, order);

        return templateEngine.process(ORDER_PLACED_TEMPLATE, context);
    }
}
