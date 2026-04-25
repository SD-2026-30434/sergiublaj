package en.sd.service.mail;

import en.sd.model.mail.SendingStatus;

public interface MailSenderService {

    SendingStatus sendHtml(String to, String subject, String htmlBody);
}
