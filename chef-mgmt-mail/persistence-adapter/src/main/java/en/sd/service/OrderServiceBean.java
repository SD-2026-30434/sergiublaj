package en.sd.service;

import en.sd.entity.OrderEntity;
import en.sd.mapper.OrderMapper;
import en.sd.model.domain.Order;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.repository.OrderRepository;
import en.sd.service.persistence.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceBean implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.ORDER_NOT_FOUND, id));
        return orderMapper.toDomain(entity);
    }
}
