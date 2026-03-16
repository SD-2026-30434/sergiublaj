package en.sd.chefmgmt.mapper;

import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefResponseDTO;
import en.sd.chefmgmt.model.ChefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ChefMapper extends DtoMapper<ChefEntity, ChefRequestDTO, ChefResponseDTO> {

    @Override
    @Mapping(target = "numberOfStars", source = "rating")
    ChefResponseDTO convertEntityToResponseDto(ChefEntity entity);

    @Mapping(target = "id", ignore = true)
    void updateChefEntity(@MappingTarget ChefEntity chefEntity, ChefRequestDTO chefRequestDTO);
}
