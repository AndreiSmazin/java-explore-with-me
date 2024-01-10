package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ErrorControllerAdvice {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ApiError> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.getBindingResult().getFieldErrors().forEach(error -> log.error("Validation error: incorrect value '{}'" +
                " of {}, {}", error.getRejectedValue(), error.getField(), error.getDefaultMessage()));

        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String reason = "Incorrectly made request.";
                    String massage = String.format("Field: %s. Error: %s. Value: %s", error.getField(),
                            error.getDefaultMessage(), error.getRejectedValue());
                    String timestamp = LocalDateTime.now().format(timeFormatter);
                    return new ApiError("BAD_REQUEST", reason, massage, timestamp);
                })
                .collect(Collectors.toList());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ApiError> onConstraintViolationException(ConstraintViolationException e) {
        e.getConstraintViolations().forEach(error -> log.error("Validation error: incorrect value" +
                " '{}' of {}, {}", error.getInvalidValue(), getFieldName(error), error.getMessage()));

        return e.getConstraintViolations().stream()
                .map(error -> {
                    String reason = "Incorrectly made request.";
                    String massage = String.format("Field: %s. Error: %s. Value: %s", getFieldName(error),
                            error.getMessage(), error.getInvalidValue());
                    String timestamp = LocalDateTime.now().format(timeFormatter);
                    return new ApiError("BAD_REQUEST", reason, massage, timestamp);
                })
                .collect(Collectors.toList());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError onDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException: {}", e.getMessage());

        String reason = "Integrity constraint has been violated.";
        String massage = e.getMessage();
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("CONFLICT", reason, massage, timestamp);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError onObjectNotFoundException(ObjectNotFoundException e) {
        log.error("ObjectNotFoundException: {}", e.getMessage());

        String reason = "The required object was not found.";
        String massage = String.format("%s with id=%s was not found", e.getObjectType(), e.getObjectId());
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("NOT_FOUND", reason, massage, timestamp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());

        String reason = "Incorrectly made request.";
        String massage = e.getMessage();
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("BAD_REQUEST", reason, massage, timestamp);
    }

    private static String getFieldName(ConstraintViolation constraintViolation) {
        String[] propertyPath = constraintViolation.getPropertyPath().toString().split("\\.");
        return propertyPath[propertyPath.length - 1];
    }
}
