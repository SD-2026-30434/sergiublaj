package en.sd.chefmgmt.repository.user;

import en.sd.chefmgmt.model.AppUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUserEntity, UUID> {

    @EntityGraph(attributePaths = "chef")
    Optional<AppUserEntity> findByUsername(String username);
}
