package en.sd.service.mail;

import en.sd.model.mail.MailRequest;
import en.sd.model.mail.MailResponse;

public interface MailService {

    MailResponse sendMail(MailRequest mailRequest);
}
