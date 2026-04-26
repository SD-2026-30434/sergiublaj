package en.sd.service.mail;

import en.sd.model.mail.ChefMailResult;

import java.util.UUID;

public interface ChefMailService {

    ChefMailResult sendChefWelcomeMail(UUID chefId);
}
