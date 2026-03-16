package en.sd.chefmgmt.service.chef;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefResponseDTO;
import en.sd.chefmgmt.exception.DataNotFoundException;
import en.sd.chefmgmt.exception.DuplicateDataException;
import en.sd.chefmgmt.exception.ExceptionCode;
import en.sd.chefmgmt.mapper.ChefMapper;
import en.sd.chefmgmt.model.ChefEntity;
import en.sd.chefmgmt.repository.chef.ChefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChefServiceBean implements ChefService {

    private final ChefRepository chefRepository;
    private final ChefMapper chefMapper;

    @Override
    public CollectionResponseDTO<ChefResponseDTO> findAll(ChefFilterDTO filter) {
        long totalElements = chefRepository.count(filter);
        List<ChefEntity> page = chefRepository.findAll(filter);
        long totalPages = filter.pageSize() > 0 ? (totalElements + filter.pageSize() - 1) / filter.pageSize() : 0;

        return CollectionResponseDTO.<ChefResponseDTO>builder()
                .pageNumber(filter.pageNumber())
                .pageSize(filter.pageSize())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .elements(chefMapper.convertEntitiesToResponseDtos(page))
                .build();
    }

    @Override
    public ChefResponseDTO findById(UUID id) {
        ChefEntity chefEntity = chefRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));

        return chefMapper.convertEntityToResponseDto(chefEntity);
    }

    @Override
    public ChefResponseDTO save(ChefRequestDTO chefRequestDTO) {
        if (chefRepository.existsByEmail(chefRequestDTO.email())) {
            throw new DuplicateDataException(ExceptionCode.EMAIL_TAKEN, chefRequestDTO.email());
        }

        ChefEntity toAdd = chefMapper.convertRequestDtoToEntity(chefRequestDTO);
        ChefEntity added = chefRepository.save(toAdd);

        return chefMapper.convertEntityToResponseDto(added);
    }

    @Override
    public ChefResponseDTO update(UUID id, ChefRequestDTO chefRequestDTO) {
        ChefEntity existing = chefRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));

        if (chefRepository.existsByEmailAndIdIsNot(chefRequestDTO.email(), id)) {
            throw new DuplicateDataException(ExceptionCode.EMAIL_TAKEN, chefRequestDTO.email());
        }

        chefMapper.updateChefEntity(existing, chefRequestDTO);

        return chefMapper.convertEntityToResponseDto(existing);
    }

    @Override
    public void delete(UUID id) {
        if (chefRepository.findById(id).isEmpty()) {
            throw new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id);
        }

        chefRepository.deleteById(id);
    }
}
