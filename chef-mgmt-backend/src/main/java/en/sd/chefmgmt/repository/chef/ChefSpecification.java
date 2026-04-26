package en.sd.chefmgmt.repository.chef;

import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.model.chef.ChefEntity;
import en.sd.chefmgmt.repository.EntitySpecification;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public final class ChefSpecification {

    public Specification<ChefEntity> byFilter(ChefFilterDTO filter) {
        return Specification.allOf(
                EntitySpecification.likeIgnoreCase("name", filter.name()),
                EntitySpecification.likeIgnoreCase("email", filter.email()),
                EntitySpecification.equalsTo("rating", filter.rating()),
                EntitySpecification.equalsTo("birthDate", filter.birthDate())
        );
    }

    public Sort bySort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, toSortProperty(sortBy));
    }

    private static String toSortProperty(String sortBy) {
        return sortBy == null
                ? "id"
                : switch (sortBy.toLowerCase()) {
            case "name" -> "name";
            case "email" -> "email";
            case "rating" -> "rating";
            case "birthdate" -> "birthDate";
            default -> "id";
        };
    }
}
