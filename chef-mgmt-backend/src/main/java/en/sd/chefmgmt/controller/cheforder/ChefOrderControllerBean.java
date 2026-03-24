package en.sd.chefmgmt.controller.cheforder;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.dto.order.OrderRequestDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;
import en.sd.chefmgmt.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/chefs/v1/{chefId}/orders")
@Slf4j
@RequiredArgsConstructor
public class ChefOrderControllerBean implements ChefOrderController {

    private final OrderService orderService;

    @Override
    public CollectionResponseDTO<OrderResponseDTO> findAllByChefId(UUID chefId, @Valid OrderFilterDTO orderFilterDTO) {
        log.info("[CHEF_ORDER] Finding all orders for chef {} with filter: {}", chefId, orderFilterDTO);

        return orderService.findAllByChefId(chefId, orderFilterDTO);
    }

    @Override
    public OrderResponseDTO save(UUID chefId, @Valid OrderRequestDTO orderRequestDTO) {
        log.info("[CHEF_ORDER] Saving order for chef {}: {}", chefId, orderRequestDTO);

        return orderService.save(chefId, orderRequestDTO);
    }

    @Override
    public OrderResponseDTO update(UUID chefId, UUID id, @Valid OrderRequestDTO orderRequestDTO) {
        log.info("[CHEF_ORDER] Updating order {} for chef {}: {}", id, chefId, orderRequestDTO);

        return orderService.update(chefId, id, orderRequestDTO);
    }

    @Override
    public void delete(UUID chefId, UUID id) {
        log.info("[CHEF_ORDER] Deleting order {} for chef {}", id, chefId);
        
        orderService.delete(chefId, id);
    }
}
