package en.sd.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // Validation & Constraint Violations
    VALIDATION_ERROR("Validation failed.", "ERR_1001"),

    // Domain Errors
    CHEF_NOT_FOUND("Chef with id %s not found.", "ERR_3001"),
    ORDER_NOT_FOUND("Order with id %s not found.", "ERR_3002"),

    // Server Errors
    SERVER_ERROR("Internal server error.", "ERR_5000");

    private final String message;
    private final String code;
}
