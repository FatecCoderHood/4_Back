package coderhood.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageException extends RuntimeException {
    public MessageException(String message) {
        super(message);
    }
}