package en.sd.controller.mail;

import en.sd.model.mail.ChefMailResult;
import en.sd.model.mail.ChefWelcomeMailRequestDTO;
import en.sd.model.mail.ChefWelcomeMailResponseDTO;
import en.sd.model.mapper.ChefMailResultMapper;
import en.sd.service.mail.ChefMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChefMailControllerBean implements ChefMailController {

    private final ChefMailService chefMailService;
    private final ChefMailResultMapper chefMailResultMapper;

    @Override
    public ChefWelcomeMailResponseDTO send(ChefWelcomeMailRequestDTO request) {
        log.info("[CHEF_MAIL] Sending chef welcome mail: {}", request);

        ChefMailResult result = chefMailService.sendChefWelcomeMail(request.chefId());

        return chefMailResultMapper.toResponse(result);
    }
}
