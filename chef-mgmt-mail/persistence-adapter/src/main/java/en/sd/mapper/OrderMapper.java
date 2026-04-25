package en.sd.mapper;

import en.sd.entity.OrderEntity;
import en.sd.model.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "chef.id", target = "chefId")
    Order toDomain(OrderEntity entity);
}
