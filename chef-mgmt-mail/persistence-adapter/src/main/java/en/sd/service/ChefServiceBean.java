package en.sd.service;

import en.sd.mapper.ChefMapper;
import en.sd.model.domain.Chef;
import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionCode;
import en.sd.repository.ChefRepository;
import en.sd.service.persistence.ChefService;
import lombok.RequiredArgsConstructor;
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
    public Chef getById(UUID id) {
        return chefRepository.findById(id)
                .map(chefMapper::toDomain)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.CHEF_NOT_FOUND, id));
    }
}
