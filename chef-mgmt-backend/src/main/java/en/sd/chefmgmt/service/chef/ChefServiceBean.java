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
import en.sd.chefmgmt.mapper.ChefWelcomeMailRequestMapper;
import en.sd.chefmgmt.model.chef.ChefEntity;
import en.sd.chefmgmt.model.mail.ChefWelcomeMailRequestDTO;
import en.sd.chefmgmt.model.mail.ChefWelcomeMailResponseDTO;
import en.sd.chefmgmt.repository.chef.ChefRepository;
import en.sd.chefmgmt.repository.chef.ChefSpecification;
import en.sd.chefmgmt.service.mail.ChefMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChefServiceBean implements ChefService {

    private final ChefRepository chefRepository;
    private final ChefMapper chefMapper;
    private final ChefMailService chefMailService;
    private final ChefWelcomeMailRequestMapper chefWelcomeMailRequestMapper;

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
//    @Transactional
    // With @Transactional, chef saved won't be commited.
    // chef-mgmt-mail won't find the chef in database and welcome mail won't be sent
    // Will be fixed with another approach in next lab
    public ChefWithOrdersResponseDTO save(ChefRequestDTO chefRequestDTO) {
        if (chefRepository.existsByEmail(chefRequestDTO.email())) {
            throw new DuplicateDataException(ExceptionCode.EMAIL_TAKEN, chefRequestDTO.email());
        }

        ChefEntity toAdd = chefMapper.convertRequestDtoToEntity(chefRequestDTO);
        ChefEntity added = chefRepository.save(toAdd);
        ChefWithOrdersResponseDTO response = chefMapper.convertEntityToResponseDto(added);

        sendChefWelcomeMail(added.getId());

        return response;
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

    private void sendChefWelcomeMail(UUID chefId) {
        ChefWelcomeMailRequestDTO mailRequest = chefWelcomeMailRequestMapper.toRequest(chefId);
        try {
            ChefWelcomeMailResponseDTO mailResponse = chefMailService.sendChefWelcomeMail(mailRequest);
            log.info("Chef welcome mail sent: chef={} to={} status={}", chefId, mailResponse.to(), mailResponse.status());
        } catch (Exception e) {
            log.error("Chef welcome mail failed: chef={}: {}", chefId, e.getMessage());
        }
    }
}
