package en.sd.service.mail;

import en.sd.model.domain.Chef;
import en.sd.model.domain.Order;

public interface MailCreationService {

    String renderOrderPlaced(Chef chef, Order order);

    String renderChefWelcome(Chef chef);
}
