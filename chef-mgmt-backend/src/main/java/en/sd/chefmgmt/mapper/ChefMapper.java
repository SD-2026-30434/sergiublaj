package en.sd.chefmgmt.mapper;

import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefWithOrdersResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefWithoutOrdersResponseDTO;
import en.sd.chefmgmt.model.ChefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = OrderMapper.class)
public interface ChefMapper extends DtoMapper<ChefEntity, ChefRequestDTO, ChefWithOrdersResponseDTO> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "userAccount", ignore = true)
    ChefEntity convertRequestDtoToEntity(ChefRequestDTO requestDto);

    @Override
    @Mapping(target = "numberOfStars", source = "rating")
    ChefWithOrdersResponseDTO convertEntityToResponseDto(ChefEntity entity);

    @Mapping(target = "numberOfStars", source = "rating")
    ChefWithoutOrdersResponseDTO convertEntityToWithoutOrdersResponseDto(ChefEntity entity);

    List<ChefWithoutOrdersResponseDTO> convertEntitiesToWithoutOrdersResponseDtos(List<ChefEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "userAccount", ignore = true)
    void updateChefEntity(@MappingTarget ChefEntity chefEntity, ChefRequestDTO chefRequestDTO);
}
