package en.sd.model.exception;

import java.time.ZonedDateTime;
import java.util.Map;

import lombok.Builder;

@Builder
public record ExceptionBody(
        ZonedDateTime timestamp,
        String code,
        String message,
        Map<String, String> details
) { }