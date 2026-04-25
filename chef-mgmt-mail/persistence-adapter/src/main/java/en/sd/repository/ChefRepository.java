package en.sd.repository;

import en.sd.entity.ChefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChefRepository extends JpaRepository<ChefEntity, UUID> {
}
