package en.sd.controller.mail;

import en.sd.model.mail.MailRequest;
import en.sd.model.mail.MailRequestDTO;
import en.sd.model.mail.MailResponse;
import en.sd.model.mail.MailResponseDTO;
import en.sd.model.mapper.MailRequestDTOMapper;
import en.sd.model.mapper.MailResponseDTOMapper;
import en.sd.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MailControllerBean implements MailController {

    private final MailService mailService;
    private final MailRequestDTOMapper mailRequestDTOMapper;
    private final MailResponseDTOMapper mailResponseDTOMapper;

    @Override
    public MailResponseDTO sendSyncMail(MailRequestDTO mailRequestDTO) {
        log.info("Sync mail request from {} to {}", mailRequestDTO.from(), mailRequestDTO.to());

        MailRequest mailRequest = mailRequestDTOMapper.convertDtoToCore(mailRequestDTO);
        MailResponse mailResponse = mailService.sendMail(mailRequest);

        return mailResponseDTOMapper.convertCoreToDto(mailResponse);
    }
}
