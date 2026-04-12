package en.sd.chefmgmt.repository;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public final class EntitySpecification {

    public <T> Specification<T> likeIgnoreCase(String field, String value) {
        return value == null || value.isBlank()
                ? (_, _, cb) -> cb.conjunction()
                : (root, _, cb) -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase().trim() + "%");
    }

    public <T, V> Specification<T> equalsTo(String field, V value) {
        return value == null
                ? (_, _, cb) -> cb.conjunction()
                : (root, _, cb) -> cb.equal(root.get(field), value);
    }
}
