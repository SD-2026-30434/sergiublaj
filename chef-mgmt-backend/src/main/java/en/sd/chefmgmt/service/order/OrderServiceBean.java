package en.sd.chefmgmt.service.order;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.dto.order.OrderRequestDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;
import en.sd.chefmgmt.exception.DataNotFoundException;
import en.sd.chefmgmt.exception.ExceptionCode;
import en.sd.chefmgmt.mapper.OrderMapper;
import en.sd.chefmgmt.model.ChefEntity;
import en.sd.chefmgmt.model.OrderEntity;
import en.sd.chefmgmt.repository.chef.ChefRepository;
import en.sd.chefmgmt.repository.order.OrderRepository;
import en.sd.chefmgmt.repository.order.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceBean implements OrderService {

    private final OrderRepository orderRepository;
    private final ChefRepository chefRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponseDTO<OrderResponseDTO> findAll(OrderFilterDTO filter) {
        Page<OrderEntity> page = orderRepository.findAll(
                OrderSpecification.byFilter(filter),
                PageRequest.of(filter.pageNumber(), filter.pageSize(), OrderSpecification.bySort(filter.sortBy(), filter.sortDirection()))
        );

        return CollectionResponseDTO.<OrderResponseDTO>builder()
                .pageNumber(filter.pageNumber())
                .pageSize(filter.pageSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .elements(orderMapper.convertEntitiesToResponseDtos(page.getContent()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponseDTO<OrderResponseDTO> findAllByChefId(UUID chefId, OrderFilterDTO filter) {
        if (chefRepository.findById(chefId).isEmpty()) {
            throw new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId);
        }

        Page<OrderEntity> page = orderRepository.findAll(
                OrderSpecification.byFilterAndChefId(filter, chefId),
                PageRequest.of(filter.pageNumber(), filter.pageSize(), OrderSpecification.bySort(filter.sortBy(), filter.sortDirection()))
        );

        return CollectionResponseDTO.<OrderResponseDTO>builder()
                .pageNumber(filter.pageNumber())
                .pageSize(filter.pageSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .elements(orderMapper.convertEntitiesToResponseDtos(page.getContent()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findById(UUID id) {
        return orderRepository.findById(id)
                .map(orderMapper::convertEntityToResponseDto)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.ORDER_NOT_FOUND, id));
    }

    @Override
    @Transactional
    public OrderResponseDTO save(UUID chefId, OrderRequestDTO orderRequestDTO) {
        ChefEntity chefEntity = chefRepository.findById(chefId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId));

        OrderEntity toAdd = orderMapper.convertRequestDtoToEntity(orderRequestDTO, chefEntity);
        OrderEntity added = orderRepository.save(toAdd);

        return orderMapper.convertEntityToResponseDto(added);
    }

    @Override
    @Transactional
    public OrderResponseDTO update(UUID chefId, UUID id, OrderRequestDTO orderRequestDTO) {
        OrderEntity existing = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.ORDER_NOT_FOUND, id));
        if (chefRepository.findById(chefId).isEmpty()) {
            throw new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId);
        }

        orderMapper.updateOrderEntity(existing, orderRequestDTO);

        return orderMapper.convertEntityToResponseDto(existing);
    }

    @Override
    @Transactional
    public void delete(UUID chefId, UUID id) {
        if (!orderRepository.existsByIdAndChefId(id, chefId)) {
            throw new DataNotFoundException(ExceptionCode.ORDER_NOT_FOUND, id);
        }

        orderRepository.deleteById(id);
    }
}
