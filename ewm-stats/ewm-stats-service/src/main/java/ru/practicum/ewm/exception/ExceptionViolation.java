package ru.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionViolation {
    private final String error;
}
