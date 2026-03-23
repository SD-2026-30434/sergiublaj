package en.sd.chefmgmt.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderRequestDTO(
        @NotBlank(message = "Item name is required and cannot be empty.")
        @Size(min = 2, max = 60, message = "Item name must be between 2 and 60 characters.")
        String itemName,

        @NotNull(message = "Total price is required.")
        @DecimalMin(value = "0.01", message = "Total price must be greater than 0.")
        Double totalPrice,

        @NotNull(message = "Order date is required.")
        ZonedDateTime orderedAt,

        @NotNull(message = "Chef id is required.")
        UUID chefId
) { }
