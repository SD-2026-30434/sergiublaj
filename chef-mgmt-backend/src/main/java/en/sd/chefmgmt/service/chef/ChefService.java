package en.sd.chefmgmt.service.chef;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefResponseDTO;

import java.util.UUID;

public interface ChefService {

    CollectionResponseDTO<ChefResponseDTO> findAll(ChefFilterDTO filter);

    ChefResponseDTO findById(UUID id);

    ChefResponseDTO save(ChefRequestDTO chefRequestDTO);

    ChefResponseDTO update(UUID id, ChefRequestDTO chefRequestDTO);

    void delete(UUID id);
}
