package ru.practicum.ewm.exception;

public class EwmStatsServerException extends RuntimeException {
    public EwmStatsServerException(String message) {
        super(message);
    }

    public EwmStatsServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EwmStatsServerException(Throwable cause) {
        super(cause);
    }
}
