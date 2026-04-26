package en.sd.service.mail;

import en.sd.model.mail.MailType;

import java.util.Map;

public interface MailCreationService {

    String render(MailType type, Map<String, Object> variables);
}
