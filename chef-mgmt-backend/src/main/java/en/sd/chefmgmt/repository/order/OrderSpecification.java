package en.sd.chefmgmt.repository.order;

import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.model.OrderEntity;
import en.sd.chefmgmt.repository.EntitySpecification;
import lombok.experimental.UtilityClass;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public final class OrderSpecification {

    public Specification<OrderEntity> byFilter(OrderFilterDTO filter) {
        return Specification.allOf(
                EntitySpecification.likeIgnoreCase("itemName", filter.itemName()),
                EntitySpecification.equalsTo("totalPrice", filter.totalPrice()),
                EntitySpecification.equalsTo("orderedAt", filter.orderedAt()),
                byChefId(filter.chefId())
        );
    }

    public Specification<OrderEntity> byFilterAndChefId(OrderFilterDTO filter, UUID chefId) {
        return Specification.allOf(
                EntitySpecification.likeIgnoreCase("itemName", filter.itemName()),
                EntitySpecification.equalsTo("totalPrice", filter.totalPrice()),
                EntitySpecification.equalsTo("orderedAt", filter.orderedAt()),
                byChefId(chefId)
        );
    }

    private Specification<OrderEntity> byChefId(UUID chefId) {
        return chefId == null
                ? (_, _, cb) -> cb.conjunction()
                : (root, _, cb) -> cb.equal(root.get("chef").get("id"), chefId);
    }

    public Sort bySort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, toSortProperty(sortBy));
    }

    private String toSortProperty(String sortBy) {
        return sortBy == null
                ? "id"
                : switch (sortBy.toLowerCase()) {
            case "itemname" -> "itemName";
            case "totalprice" -> "totalPrice";
            case "orderedat" -> "orderedAt";
            case "chefid" -> "chef.id";
            default -> "id";
        };
    }
}
