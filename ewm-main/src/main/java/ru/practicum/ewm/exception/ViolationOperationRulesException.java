package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class ViolationOperationRulesException extends RuntimeException {
    private String field;
    private String value;

    public ViolationOperationRulesException(String message) {
        super(message);
    }

    public ViolationOperationRulesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViolationOperationRulesException(Throwable cause) {
        super(cause);
    }
}
