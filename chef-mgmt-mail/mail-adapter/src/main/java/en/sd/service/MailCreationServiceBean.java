package en.sd.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import en.sd.model.mail.MailRequest;
import en.sd.service.mail.MailCreationService;
import en.sd.util.MailUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MailCreationServiceBean implements MailCreationService {

    @Override
    public String createMail(MailRequest mailRequest) throws IOException {
        String emailContent = getEmailContent();
        Map<String, String> emailProperties = getEmailProperties(mailRequest);

        return emailProperties.entrySet().stream().reduce(
                emailContent,
                (content, entry) -> content.replace(entry.getKey(), entry.getValue()),
                (_, s2) -> s2
        );
    }

    private String getEmailContent() throws IOException {
        Resource resource = new ClassPathResource(MailUtils.EMAIL_TEMPLATE_LOCATION);

        return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private Map<String, String> getEmailProperties(MailRequest mailRequest) {
        return Map.ofEntries(
                Map.entry(MailUtils.EMAIL_SUBJECT, mailRequest.subject()),
                Map.entry(MailUtils.EMAIL_BODY, mailRequest.body())
        );
    }
}
