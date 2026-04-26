package en.sd.chefmgmt.service.order;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.dto.order.OrderRequestDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;
import en.sd.chefmgmt.exception.DataNotFoundException;
import en.sd.chefmgmt.exception.ExceptionCode;
import en.sd.chefmgmt.mapper.OrderMapper;
import en.sd.chefmgmt.mapper.SendOrderMailRequestMapper;
import en.sd.chefmgmt.model.chef.ChefEntity;
import en.sd.chefmgmt.model.mail.SendOrderMailRequestDTO;
import en.sd.chefmgmt.model.mail.SendOrderMailResponseDTO;
import en.sd.chefmgmt.model.order.OrderEntity;
import en.sd.chefmgmt.repository.chef.ChefRepository;
import en.sd.chefmgmt.repository.order.OrderRepository;
import en.sd.chefmgmt.repository.order.OrderSpecification;
import en.sd.chefmgmt.service.mail.OrderMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceBean implements OrderService {

    private final OrderRepository orderRepository;
    private final ChefRepository chefRepository;
    private final OrderMapper orderMapper;
    private final OrderMailService mailService;
    private final SendOrderMailRequestMapper sendOrderMailRequestMapper;

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
//    @Transactional
    // With @Transactional, order saved won't be commited.
    // chef-mgmt-mail won't find the order in database and mail won't be sent
    // Will be fixed with another approach in next lab
    public OrderResponseDTO save(UUID chefId, OrderRequestDTO orderRequestDTO) {
        ChefEntity chefEntity = chefRepository.findById(chefId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, chefId));

        OrderEntity toAdd = orderMapper.convertRequestDtoToEntity(orderRequestDTO, chefEntity);
        OrderEntity added = orderRepository.save(toAdd);
        OrderResponseDTO response = orderMapper.convertEntityToResponseDto(added);

        sendOrderMail(chefEntity.getId(), added.getId());

        return response;
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

    private void sendOrderMail(UUID chefId, UUID orderId) {
        SendOrderMailRequestDTO mailRequest = sendOrderMailRequestMapper.toRequest(chefId, orderId);
        try {
            SendOrderMailResponseDTO mailResponse = mailService.sendOrderMail(mailRequest);
            log.info("Order mail sent: order={} to={} status={}", orderId, mailResponse.to(), mailResponse.status());
        } catch (Exception e) {
            log.error("Order mail failed: order={} chef={}: {}", orderId, chefId, e.getMessage());
        }
    }
}
