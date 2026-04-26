package en.sd.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.UtilityClass;
import org.springframework.mail.javamail.MimeMessageHelper;

@UtilityClass
public class MailUtil {

    public void createMimeMessageHelper(String from, String to, String subject, MimeMessage message, String htmlBody)
            throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
    }
}
