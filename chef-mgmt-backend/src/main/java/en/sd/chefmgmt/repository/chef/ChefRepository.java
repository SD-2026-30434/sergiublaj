package en.sd.chefmgmt.repository.chef;

import en.sd.chefmgmt.model.ChefEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ChefRepository extends JpaRepository<ChefEntity, UUID>, JpaSpecificationExecutor<ChefEntity> {

    @EntityGraph(attributePaths = "orders")
    Page<ChefEntity> findAll(@NonNull Specification<ChefEntity> spec, @NonNull Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdIsNot(String email, UUID id);
}
