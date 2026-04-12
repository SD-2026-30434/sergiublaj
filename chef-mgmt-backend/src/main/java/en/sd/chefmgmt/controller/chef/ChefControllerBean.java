package en.sd.chefmgmt.controller.chef;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefWithOrdersResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefWithoutOrdersResponseDTO;
import en.sd.chefmgmt.service.chef.ChefService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/chefs/v1")
@Slf4j
@RequiredArgsConstructor
public class ChefControllerBean implements ChefController {

    private final ChefService chefService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CollectionResponseDTO<ChefWithoutOrdersResponseDTO> findAll(ChefFilterDTO chefFilterDTO) {
        log.info("[CHEF] Finding all chefs: {}", chefFilterDTO);

        return chefService.findAll(chefFilterDTO);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CHEF') and @authz.isOwnChef(#id))")
    public ChefWithOrdersResponseDTO findById(UUID id) {
        log.info("[CHEF] Finding chef by id: {}", id);

        return chefService.findById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ChefWithOrdersResponseDTO save(ChefRequestDTO chefRequestDTO) {
        log.info("[CHEF] Saving chef: {}", chefRequestDTO);

        return chefService.save(chefRequestDTO);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CHEF') and @authz.isOwnChef(#id))")
    public ChefWithOrdersResponseDTO update(UUID id, ChefRequestDTO chefRequestDTO) {
        log.info("[CHEF] Updating chef: {}", chefRequestDTO);

        return chefService.update(id, chefRequestDTO);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        log.info("[CHEF] Deleting chef: {}", id);

        chefService.delete(id);
    }
}
