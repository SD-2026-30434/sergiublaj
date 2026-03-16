package en.sd.chefmgmt.dto.chef;

import en.sd.chefmgmt.validator.ValidBirthDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;

public record ChefRequestDTO(
        @NotBlank(message = "Name is required and cannot be empty.")
        @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters.")
        String name,

        @Min(value = 0, message = "Rating must be at least 0.")
        @Max(value = 5, message = "Rating must not exceed 5.")
        Double rating,

        @NotBlank(message = "Email is required and cannot be empty.")
        @Email(message = "Invalid email format.")
        String email,

        @ValidBirthDate
        ZonedDateTime birthDate
) { }
