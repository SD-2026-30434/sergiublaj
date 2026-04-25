package en.sd.service;

import en.sd.model.mail.MailRequest;
import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailSenderService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceBean implements MailSenderService {

    private final JavaMailSender javaMailSender;

    @Override
    public SendingStatus sendMail(MailRequest mailRequest, String content) {
        try {
            MimeMessage message = buildMimeMessage(mailRequest, content);
            javaMailSender.send(message);
            return SendingStatus.SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage());
            return SendingStatus.FAILURE;
        }
    }

    private MimeMessage buildMimeMessage(MailRequest mailRequest, String emailContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(mailRequest.from());
        message.setRecipients(Message.RecipientType.TO, mailRequest.to());
        message.setSubject(mailRequest.subject());
        message.setContent(emailContent, MimeTypeUtils.TEXT_HTML_VALUE);

        return message;
    }
}