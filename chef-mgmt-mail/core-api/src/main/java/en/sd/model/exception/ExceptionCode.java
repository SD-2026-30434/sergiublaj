package en.sd.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // Validation & Constraint Violations
    VALIDATION_ERROR("Validation failed.", "ERR_1001"),

    // Mail Errors
    SYNC_MAIL_ERROR("Sync mail error.", "ERR_2001"),

    // User Errors
    USER_NOT_FOUND("User with email %s not found.", "ERR_3001"),

    // Server Errors
    SERVER_ERROR("Internal server error.", "ERR_5000");

    private final String message;
    private final String code;
}