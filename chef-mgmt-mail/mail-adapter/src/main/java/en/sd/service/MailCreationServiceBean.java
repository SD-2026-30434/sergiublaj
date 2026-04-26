package en.sd.service;

import en.sd.model.mail.MailType;
import en.sd.service.mail.MailCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailCreationServiceBean implements MailCreationService {

    private final SpringTemplateEngine templateEngine;

    // Each entry in `variables` becomes a Thymeleaf variable available in the template
    // (e.g. "chef" -> ${chef.name()}). The MailType picks the .html template under
    // resources/templates/, and the engine substitutes the placeholders to produce the body.
    @Override
    public String render(MailType type, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);

        return templateEngine.process(type.getTemplate(), context);
    }
}
