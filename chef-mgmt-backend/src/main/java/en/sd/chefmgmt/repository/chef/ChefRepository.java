package en.sd.chefmgmt.repository.chef;

import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.model.ChefEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory repository: a class holding a list of entities with CRUD and filter support.
 */
@Repository
public class ChefRepository {

    private final List<ChefEntity> entities = new ArrayList<>();

    private static boolean matchesFilter(ChefEntity e, ChefFilterDTO f) {
        if (f.name() != null && !f.name().isBlank()
                && (e.getName() == null || !e.getName().toLowerCase().contains(f.name().toLowerCase().trim()))) {
            return false;
        }
        if (f.email() != null && !f.email().isBlank()
                && (e.getEmail() == null || !e.getEmail().toLowerCase().contains(f.email().toLowerCase().trim()))) {
            return false;
        }
        if (f.rating() != null && Double.compare(e.getRating(), f.rating()) != 0) {
            return false;
        }

        return f.birthDate() == null || (e.getBirthDate() != null && e.getBirthDate().toInstant().equals(f.birthDate().toInstant()));
    }

    public List<ChefEntity> findAll(ChefFilterDTO filter) {
        return entities.stream().filter(e -> matchesFilter(e, filter))
                .skip((long) filter.pageNumber() * filter.pageSize())
                .limit(filter.pageSize())
                .toList();
    }

    public long count(ChefFilterDTO filter) {
        return entities.stream().filter(e -> matchesFilter(e, filter)).count();
    }

    public Optional<ChefEntity> findById(UUID id) {
        return entities.stream().filter(e -> Objects.equals(e.getId(), id)).findFirst();
    }

    public ChefEntity save(ChefEntity entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        entities.add(entity);

        return entity;
    }

    public void deleteById(UUID id) {
        entities.removeIf(e -> Objects.equals(e.getId(), id));
    }

    public boolean existsByEmail(String email) {
        return entities.stream().anyMatch(e -> Objects.equals(email, e.getEmail()));
    }

    public boolean existsByEmailAndIdIsNot(String email, UUID id) {
        return entities.stream()
                .anyMatch(e -> Objects.equals(email, e.getEmail()) && !Objects.equals(id, e.getId()));
    }
}
