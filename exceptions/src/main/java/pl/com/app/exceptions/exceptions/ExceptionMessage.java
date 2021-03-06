package pl.com.app.exceptions.exceptions;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ExceptionMessage {
    private ExceptionInfo exceptionInfo;
    private String path;
}
