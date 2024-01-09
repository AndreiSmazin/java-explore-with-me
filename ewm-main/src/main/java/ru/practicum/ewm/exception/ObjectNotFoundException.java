package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class ObjectNotFoundException extends RuntimeException {
    private String objectType;
    private long objectId;

    public ObjectNotFoundException(String objectType, long objectId, String message) {
        super(message);
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public ObjectNotFoundException(String objectType, long objectId, String message, Throwable cause) {
        super(message, cause);
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public ObjectNotFoundException(String objectType, long objectId, Throwable cause) {
        super(cause);
        this.objectType = objectType;
        this.objectId = objectId;
    }
}
