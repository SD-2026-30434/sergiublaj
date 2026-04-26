package en.sd.chefmgmt.repository.user;

import en.sd.chefmgmt.model.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = "chef")
    Optional<UserEntity> findByEmail(String email);
}
