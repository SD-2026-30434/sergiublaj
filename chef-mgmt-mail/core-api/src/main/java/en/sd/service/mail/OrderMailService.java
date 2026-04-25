package en.sd.service.mail;

import en.sd.model.mail.OrderMailResult;

import java.util.UUID;

public interface OrderMailService {

    OrderMailResult sendOrderMail(UUID chefId, UUID orderId);
}
