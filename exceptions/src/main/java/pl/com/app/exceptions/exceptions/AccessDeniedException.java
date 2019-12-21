package pl.com.app.exceptions.exceptions;

public class AccessDeniedException extends MyException {
    public AccessDeniedException(ExceptionCode exceptionCode, String exceptionMessage) {
        super(exceptionCode, exceptionMessage);
    }
}
