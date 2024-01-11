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
        List<String> messages = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s", error.getField(),
                        error.getDefaultMessage(), error.getRejectedValue()))
                .peek(message -> log.error("MethodArgumentNotValidException: " + message))
                .collect(Collectors.toList());

        String reason = "Incorrectly made request.";

        return messages.stream()
                .map(message -> {
                    String timestamp = LocalDateTime.now().format(timeFormatter);
                    return new ApiError("BAD_REQUEST", reason, message, timestamp);
                })
                .collect(Collectors.toList());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ApiError> onConstraintViolationException(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s", getFieldName(error),
                        error.getMessage(), error.getInvalidValue()))
                .peek(message -> log.error("ConstraintViolationException: " + message))
                .collect(Collectors.toList());

        String reason = "Incorrectly made request.";

        return messages.stream()
                .map(message -> {
                    String timestamp = LocalDateTime.now().format(timeFormatter);
                    return new ApiError("BAD_REQUEST", reason, message, timestamp);
                })
                .collect(Collectors.toList());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError onDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage();
        log.error("DataIntegrityViolationException: {}", message);

        String reason = "Integrity constraint has been violated.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("CONFLICT", reason, message, timestamp);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError onObjectNotFoundException(ObjectNotFoundException e) {
        String message = String.format("%s with id=%s was not found", e.getObjectType(), e.getObjectId());
        log.error("ObjectNotFoundException: {}", message);

        String reason = "The required object was not found.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("NOT_FOUND", reason, message, timestamp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = e.getMessage();
        log.error("MethodArgumentTypeMismatchException: {}", message);

        String reason = "Incorrectly made request.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("BAD_REQUEST", reason, message, timestamp);
    }

    @ExceptionHandler(ViolationOperationRulesException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ApiError onViolationOperationRulesException(ViolationOperationRulesException e) {
        String message = e.getMessage();
        log.error("ViolationOperationRulesException: {}", message);

        String reason = "For the requested operation the conditions are not met.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return new ApiError("FORBIDDEN", reason, message, timestamp);
    }

    private static String getFieldName(ConstraintViolation constraintViolation) {
        String[] propertyPath = constraintViolation.getPropertyPath().toString().split("\\.");
        return propertyPath[propertyPath.length - 1];
    }
}
