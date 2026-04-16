package en.sd.chefmgmt.mapper;

import en.sd.chefmgmt.dto.order.OrderRequestDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;
import en.sd.chefmgmt.model.ChefEntity;
import en.sd.chefmgmt.model.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface OrderMapper extends DtoMapper<OrderEntity, OrderRequestDTO, OrderResponseDTO> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chef", ignore = true)
    OrderEntity convertRequestDtoToEntity(OrderRequestDTO requestDto);

    default OrderEntity convertRequestDtoToEntity(OrderRequestDTO requestDto, ChefEntity chefEntity) {
        OrderEntity orderEntity = convertRequestDtoToEntity(requestDto);
        orderEntity.setChef(chefEntity);
        return orderEntity;
    }

    @Override
    @Mapping(target = "chefId", source = "chef.id")
    @Mapping(target = "chefName", source = "chef.name")
    OrderResponseDTO convertEntityToResponseDto(OrderEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chef", ignore = true)
    void updateOrderEntity(@MappingTarget OrderEntity orderEntity, OrderRequestDTO orderRequestDTO);
}
