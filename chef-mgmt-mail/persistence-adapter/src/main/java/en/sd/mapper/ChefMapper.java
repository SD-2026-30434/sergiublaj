package en.sd.mapper;

import en.sd.entity.ChefEntity;
import en.sd.model.domain.Chef;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChefMapper {

    Chef toDomain(ChefEntity entity);
}
