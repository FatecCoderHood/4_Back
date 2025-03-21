package coderhood.exception;

import lombok.Getter;

@Getter
public class MessageException extends RuntimeException {
    private final String message;

    public MessageException(String message) {
        super(message);
        this.message = message;
    }

}