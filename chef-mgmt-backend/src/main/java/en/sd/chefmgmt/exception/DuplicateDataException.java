package en.sd.chefmgmt.exception;

import lombok.Getter;

@Getter
public class DuplicateDataException extends RuntimeException {

    private final String code;

    public DuplicateDataException(ExceptionCode exceptionCode, Object... args) {
        super(String.format(exceptionCode.getMessage(), args));
        this.code = exceptionCode.getCode();
    }
}