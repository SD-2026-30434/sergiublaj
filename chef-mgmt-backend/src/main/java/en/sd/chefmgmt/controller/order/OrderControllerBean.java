package en.sd.chefmgmt.controller.order;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;
import en.sd.chefmgmt.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders/v1")
@Slf4j
@RequiredArgsConstructor
public class OrderControllerBean implements OrderController {

    private final OrderService orderService;

    @Override
    public CollectionResponseDTO<OrderResponseDTO> findAll(OrderFilterDTO orderFilterDTO) {
        log.info("[ORDER] Finding all orders with filter: {}", orderFilterDTO);

        return orderService.findAll(orderFilterDTO);
    }

    @Override
    public OrderResponseDTO findById(UUID id) {
        log.info("[ORDER] Finding order by id: {}", id);

        return orderService.findById(id);
    }
}
