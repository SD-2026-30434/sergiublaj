package en.sd.chefmgmt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // Validation & Constraint Violations
    VALIDATION_ERROR("Validation failed.", "ERR_1001"),
    CONSTRAINT_VIOLATION("Constraint violation.", "ERR_1002"),

    // Chef Errors
    CHEF_NOT_FOUND("Chef %s not found.", "ERR_2001"),
    EMAIL_TAKEN("Email %s is already taken.", "ERR_2002"),
    
    // Order Errors
    ORDER_NOT_FOUND("Order %s not found.", "ERR_3001"),

    // Authorization
    ACCESS_DENIED("You are not allowed to perform this action.", "ERR_4001"),
    INVALID_CREDENTIALS("Invalid credentials.", "ERR_4002"),
    MISSING_CREDENTIALS("Email and password are required.", "ERR_4003"),

    // Server Errors
    SERVER_ERROR("Internal server error.", "ERR_5000");

    private final String message;
    private final String code;
}