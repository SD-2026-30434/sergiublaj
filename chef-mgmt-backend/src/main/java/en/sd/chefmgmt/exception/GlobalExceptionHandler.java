package en.sd.chefmgmt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "Invalid value";
    private static final String VALIDATION_FAILED_ERROR_MESSAGE = "Validation failed";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionBody handleValidationErrors(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), DEFAULT_ERROR_MESSAGE),
                        (existing, _) -> existing
                ));

        return ExceptionBody.builder()
                .timestamp(ZonedDateTime.now())
                .code(ExceptionCode.VALIDATION_ERROR.getCode())
                .message(VALIDATION_FAILED_ERROR_MESSAGE)
                .details(errors)
                .build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ExceptionBody handleDataNotFound(DataNotFoundException exception) {
        return ExceptionBody.builder()
                .timestamp(ZonedDateTime.now())
                .code(exception.getCode())
                .message(exception.getMessage())
                .details(Collections.emptyMap())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied(AccessDeniedException ignored) {
        return ExceptionBody.of(ExceptionCode.ACCESS_DENIED);
    }

    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ExceptionBody handleDuplicateData(DuplicateDataException exception) {
        return ExceptionBody.builder()
                .timestamp(ZonedDateTime.now())
                .code(exception.getCode())
                .message(exception.getMessage())
                .details(Collections.emptyMap())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleGlobalException(Exception exception) {
        return ExceptionBody.builder()
                .timestamp(ZonedDateTime.now())
                .code(ExceptionCode.SERVER_ERROR.getCode())
                .message(ExceptionCode.SERVER_ERROR.getMessage() + " " + exception.getMessage())
                .details(Collections.emptyMap())
                .build();
    }
}
