package en.sd.service;

import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailSenderService;
import en.sd.util.MailUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceBean implements MailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Override
    public SendingStatus sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MailUtil.createMimeMessageHelper(fromAddress, to, subject, message, htmlBody);
            javaMailSender.send(message);

            return SendingStatus.SUCCESS;
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage());
            return SendingStatus.FAILURE;
        }
    }
}
