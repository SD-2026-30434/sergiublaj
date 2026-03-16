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

    // Server Errors
    SERVER_ERROR("Internal server error.", "ERR_5000");

    private final String message;
    private final String code;
}