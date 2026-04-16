package en.sd.chefmgmt.dto.order;

import en.sd.chefmgmt.util.RestUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public record OrderFilterDTO(
        String itemName,
        Double totalPrice,
        ZonedDateTime orderedAt,
        UUID chefId,

        @Pattern(
                regexp = "(?i)id|itemName|totalPrice|orderedAt|chefName",
                message = "Sort by must be one of: id, itemName, totalPrice, orderedAt, chefName."
        )
        String sortBy,

        @Pattern(regexp = "(?i)asc|desc", message = "Sort direction must be ASC or DESC.")
        String sortDirection,

        @Min(value = 0, message = "Page number must be at least 0.")
        Integer pageNumber,

        @Min(value = 1, message = "Page size must be at least 1.")
        Integer pageSize
) {
    public OrderFilterDTO {
        sortBy = Objects.requireNonNullElse(sortBy, "id");
        sortDirection = Objects.requireNonNullElse(sortDirection, "asc");
        pageNumber = Objects.requireNonNullElse(pageNumber, RestUtil.DEFAULT_PAGE_NUMBER);
        pageSize = Objects.requireNonNullElse(pageSize, RestUtil.DEFAULT_PAGE_SIZE);
    }
}
