package en.sd.service.mail;

import en.sd.model.mail.MailRequest;
import en.sd.model.mail.SendingStatus;

public interface MailSenderService {

    SendingStatus sendMail(MailRequest mailRequest, String content);
}
