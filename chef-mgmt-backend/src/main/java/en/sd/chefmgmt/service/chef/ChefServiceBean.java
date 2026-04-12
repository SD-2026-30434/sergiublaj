package en.sd.chefmgmt.service.chef;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefWithOrdersResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefWithoutOrdersResponseDTO;
import en.sd.chefmgmt.exception.DataNotFoundException;
import en.sd.chefmgmt.exception.DuplicateDataException;
import en.sd.chefmgmt.exception.ExceptionCode;
import en.sd.chefmgmt.mapper.ChefMapper;
import en.sd.chefmgmt.model.ChefEntity;
import en.sd.chefmgmt.repository.chef.ChefRepository;
import en.sd.chefmgmt.repository.chef.ChefSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChefServiceBean implements ChefService {

    private final ChefRepository chefRepository;
    private final ChefMapper chefMapper;

    @Override
    @Transactional(readOnly = true)
    public CollectionResponseDTO<ChefWithoutOrdersResponseDTO> findAll(ChefFilterDTO filter) {
        Specification<ChefEntity> specification = ChefSpecification.byFilter(filter);
        Page<ChefEntity> page = chefRepository.findAll(
                specification,
                PageRequest.of(filter.pageNumber(), filter.pageSize(), ChefSpecification.bySort(filter.sortBy(), filter.sortDirection()))
        );

        return CollectionResponseDTO.<ChefWithoutOrdersResponseDTO>builder()
                .pageNumber(filter.pageNumber())
                .pageSize(filter.pageSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .elements(chefMapper.convertEntitiesToWithoutOrdersResponseDtos(page.getContent()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChefWithOrdersResponseDTO findById(UUID id) {
        return chefRepository.findById(id)
                .map(chefMapper::convertEntityToResponseDto)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));
    }

    @Override
    @Transactional
    public ChefWithOrdersResponseDTO save(ChefRequestDTO chefRequestDTO) {
        if (chefRepository.existsByEmail(chefRequestDTO.email())) {
            throw new DuplicateDataException(ExceptionCode.EMAIL_TAKEN, chefRequestDTO.email());
        }

        ChefEntity toAdd = chefMapper.convertRequestDtoToEntity(chefRequestDTO);
        ChefEntity added = chefRepository.save(toAdd);

        return chefMapper.convertEntityToResponseDto(added);
    }

    @Override
    @Transactional
    public ChefWithOrdersResponseDTO update(UUID id, ChefRequestDTO chefRequestDTO) {
        ChefEntity existing = chefRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));

        if (chefRepository.existsByEmailAndIdIsNot(chefRequestDTO.email(), id)) {
            throw new DuplicateDataException(ExceptionCode.EMAIL_TAKEN, chefRequestDTO.email());
        }

        chefMapper.updateChefEntity(existing, chefRequestDTO);

        return chefMapper.convertEntityToResponseDto(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (chefRepository.findById(id).isEmpty()) {
            throw new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id);
        }

        chefRepository.deleteById(id);
    }
}
