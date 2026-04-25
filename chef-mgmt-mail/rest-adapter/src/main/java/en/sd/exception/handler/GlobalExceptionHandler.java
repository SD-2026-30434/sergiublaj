package en.sd.exception.handler;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import en.sd.model.exception.DataNotFoundException;
import en.sd.model.exception.ExceptionBody;
import en.sd.model.exception.ExceptionCode;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
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
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleDataNotFoundException(DataNotFoundException exception) {
        return ExceptionBody.builder()
                .timestamp(ZonedDateTime.now())
                .code(exception.getCode())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleGlobalException(Exception exception) {
        return ExceptionBody.builder()
                .timestamp(ZonedDateTime.now())
                .code(ExceptionCode.SERVER_ERROR.getCode())
                .message(exception.getMessage())
                .build();
    }
}