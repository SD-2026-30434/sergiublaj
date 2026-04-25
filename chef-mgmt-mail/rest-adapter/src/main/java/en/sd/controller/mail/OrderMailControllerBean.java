package en.sd.controller.mail;

import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendOrderMailRequestDTO;
import en.sd.model.mail.SendOrderMailResponseDTO;
import en.sd.model.mapper.OrderMailResultMapper;
import en.sd.service.mail.OrderMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderMailControllerBean implements OrderMailController {

    private final OrderMailService orderMailService;
    private final OrderMailResultMapper orderMailResultMapper;

    @Override
    public SendOrderMailResponseDTO send(SendOrderMailRequestDTO request) {
        OrderMailResult result = orderMailService.sendOrderMail(request.chefId(), request.orderId());
        return orderMailResultMapper.toResponse(result);
    }
}
