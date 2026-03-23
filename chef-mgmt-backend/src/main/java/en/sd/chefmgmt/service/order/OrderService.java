package en.sd.chefmgmt.service.order;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.dto.order.OrderRequestDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;

import java.util.UUID;

public interface OrderService {

    CollectionResponseDTO<OrderResponseDTO> findAll(OrderFilterDTO filter);

    OrderResponseDTO findById(UUID id);

    OrderResponseDTO save(OrderRequestDTO orderRequestDTO);

    OrderResponseDTO update(UUID id, OrderRequestDTO orderRequestDTO);

    void delete(UUID id);
}
