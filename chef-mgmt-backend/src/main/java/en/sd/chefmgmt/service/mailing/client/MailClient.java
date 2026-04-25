package en.sd.chefmgmt.service.mailing.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1/mails")
public interface MailClient {

    @PostExchange("/orders")
    SendOrderMailResponseDTO sendOrderMail(@RequestBody SendOrderMailRequestDTO request);
}
