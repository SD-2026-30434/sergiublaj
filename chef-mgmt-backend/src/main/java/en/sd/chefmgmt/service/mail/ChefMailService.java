package en.sd.chefmgmt.service.mail;

import en.sd.chefmgmt.model.mail.ChefWelcomeMailRequestDTO;
import en.sd.chefmgmt.model.mail.ChefWelcomeMailResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("${chef-mgmt-mail.paths.base}")
public interface ChefMailService {

    @PostExchange("${chef-mgmt-mail.paths.chefs}")
    ChefWelcomeMailResponseDTO sendChefWelcomeMail(@RequestBody ChefWelcomeMailRequestDTO request);
}
