package en.sd.chefmgmt.service.chef;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefWithOrdersResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefWithoutOrdersResponseDTO;

import java.util.UUID;

public interface ChefService {

    CollectionResponseDTO<ChefWithoutOrdersResponseDTO> findAll(ChefFilterDTO filter);

    ChefWithOrdersResponseDTO findById(UUID id);

    ChefWithOrdersResponseDTO save(ChefRequestDTO chefRequestDTO);

    ChefWithOrdersResponseDTO update(UUID id, ChefRequestDTO chefRequestDTO);

    void delete(UUID id);
}
