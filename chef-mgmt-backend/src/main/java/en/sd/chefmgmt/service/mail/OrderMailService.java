package en.sd.chefmgmt.service.mail;

import en.sd.chefmgmt.model.mail.SendOrderMailRequestDTO;
import en.sd.chefmgmt.model.mail.SendOrderMailResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("${chef-mgmt-mail.paths.base}")
public interface OrderMailService {

    @PostExchange("${chef-mgmt-mail.paths.orders}")
    SendOrderMailResponseDTO sendOrderMail(@RequestBody SendOrderMailRequestDTO request);
}
