package en.sd.service;

import en.sd.model.domain.Chef;
import en.sd.model.mail.ChefMailResult;
import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.ChefMailService;
import en.sd.service.mail.MailCreationService;
import en.sd.service.mail.MailSenderService;
import en.sd.service.persistence.ChefService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChefMailServiceBean implements ChefMailService {

    private static final String CHEF_WELCOME_SUBJECT = "Welcome to Chef Management";

    private final ChefService chefService;
    private final MailCreationService mailCreationService;
    private final MailSenderService mailSenderService;

    @Override
    public ChefMailResult sendChefWelcomeMail(UUID chefId) {
        Chef chef = chefService.getById(chefId);
        String htmlBody = mailCreationService.renderChefWelcome(chef);
        SendingStatus status = mailSenderService.sendHtml(chef.email(), CHEF_WELCOME_SUBJECT, htmlBody);
        UUID correlationId = UUID.randomUUID();
        log.info("Chef welcome mail dispatched: id={} chef={} to={} status={}", correlationId, chefId, chef.email(), status);

        return new ChefMailResult(correlationId, chef.email(), status);
    }
}
