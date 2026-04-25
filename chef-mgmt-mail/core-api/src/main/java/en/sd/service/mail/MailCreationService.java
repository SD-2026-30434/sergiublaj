package en.sd.service.mail;

import java.io.IOException;

import en.sd.model.mail.MailRequest;

public interface MailCreationService {

    String createMail(MailRequest mailRequest) throws IOException;
}
