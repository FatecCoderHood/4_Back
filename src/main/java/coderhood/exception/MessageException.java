package coderhood.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageException extends RuntimeException {
    private final String message;

}