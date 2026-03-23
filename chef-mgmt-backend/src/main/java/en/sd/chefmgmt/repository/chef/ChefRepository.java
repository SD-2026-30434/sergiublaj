package en.sd.chefmgmt.repository.chef;

import en.sd.chefmgmt.model.ChefEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ChefRepository extends JpaRepository<ChefEntity, UUID>, JpaSpecificationExecutor<ChefEntity> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdIsNot(String email, UUID id);
}
