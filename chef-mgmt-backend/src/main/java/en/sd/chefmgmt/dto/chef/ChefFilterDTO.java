package en.sd.chefmgmt.dto.chef;

import en.sd.chefmgmt.util.RestUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.time.ZonedDateTime;
import java.util.Objects;

public record ChefFilterDTO(
        String name,
        Double rating,
        String email,
        ZonedDateTime birthDate,

        @Pattern(
                regexp = "(?i)id|name|email|rating|birthDate",
                message = "Sort by must be one of: id, name, email, rating, birthDate."
        )
        String sortBy,

        @Pattern(regexp = "(?i)asc|desc", message = "Sort direction must be ASC or DESC.")
        String sortDirection,

        @Min(value = 0, message = "Page number must be at least 0.")
        Integer pageNumber,

        @Min(value = 1, message = "Page size must be at least 1.")
        Integer pageSize
) {

    public ChefFilterDTO {
        sortBy = Objects.requireNonNullElse(sortBy, "id");
        sortDirection = Objects.requireNonNullElse(sortDirection, "asc");
        pageNumber = Objects.requireNonNullElse(pageNumber, RestUtil.DEFAULT_PAGE_NUMBER);
        pageSize = Objects.requireNonNullElse(pageSize, RestUtil.DEFAULT_PAGE_SIZE);
    }
}