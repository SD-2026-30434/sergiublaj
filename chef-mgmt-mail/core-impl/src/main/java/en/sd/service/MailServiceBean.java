package en.sd.service;

import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.model.mail.MailRequest;
import en.sd.model.mail.MailResponse;
import en.sd.model.mail.SendingStatus;
import en.sd.service.mail.MailCreationService;
import en.sd.service.mail.MailSenderService;
import en.sd.service.mail.MailService;
import en.sd.service.persistence.UserService;
import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceBean implements MailService {

    private final UserService userService;
    private final MailCreationService mailCreationService;
    private final MailSenderService mailSenderService;

    @Override
    public MailResponse sendMail(MailRequest mailRequest) {
        checkUserExistence(mailRequest.from());
        checkUserExistence(mailRequest.to());

        try {
            String mailContent = mailCreationService.createMail(mailRequest);
            SendingStatus status = mailSenderService.sendMail(mailRequest, mailContent);

//            log.info("Sent mail {} ---> {}, status: {}", mailRequest.from(), mailRequest.to(), status);
            return new MailResponse(mailRequest.from(), mailRequest.to(), status);
        } catch (Exception e) {
//            log.error("Error while sending mail {} ---> {}: ", e.getMessage());
            return new MailResponse(mailRequest.from(), mailRequest.to(), SendingStatus.FAILURE);
        }
    }

    private void checkUserExistence(String email) {
        if (!userService.existsByEmail(email)) {
//            log.error("User with email {} does not exist", email);
            throw new DataNotFoundException(ExceptionCode.USER_NOT_FOUND, email);
        }
    }
}
