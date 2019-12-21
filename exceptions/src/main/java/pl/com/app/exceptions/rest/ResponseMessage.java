package pl.com.app.exceptions.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.app.exceptions.exceptions.ExceptionMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessage<T> {
    private T data;
    private ExceptionMessage exceptionMessage;
}
