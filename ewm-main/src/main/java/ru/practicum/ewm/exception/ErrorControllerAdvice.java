package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorControllerAdvice {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ApiError>> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> messages = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s", error.getField(),
                        error.getDefaultMessage(), error.getRejectedValue()))
                .peek(message -> log.error("MethodArgumentNotValidException: {}", message))
                .collect(Collectors.toList());

        String reason = "Incorrectly made request.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messages.stream()
                        .map(message -> {
                            String timestamp = LocalDateTime.now().format(timeFormatter);
                            return new ApiError("BAD_REQUEST", reason, message, timestamp);
                        })
                        .collect(Collectors.toList()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<ApiError>> onBindException(BindException e) {
        List<String> messages = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s", error.getField(),
                        error.getDefaultMessage(), error.getRejectedValue()))
                .peek(message -> log.error("BindException: {}", message))
                .collect(Collectors.toList());

        String reason = "Incorrectly made request.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messages.stream()
                        .map(message -> {
                            String timestamp = LocalDateTime.now().format(timeFormatter);
                            return new ApiError("BAD_REQUEST", reason, message, timestamp);
                        })
                        .collect(Collectors.toList()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ApiError>> onConstraintViolationException(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s", getFieldName(error),
                        error.getMessage(), error.getInvalidValue()))
                .peek(message -> log.error("ConstraintViolationException: {}", message))
                .collect(Collectors.toList());

        String reason = "Incorrectly made request.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messages.stream()
                        .map(message -> {
                            String timestamp = LocalDateTime.now().format(timeFormatter);
                            return new ApiError("BAD_REQUEST", reason, message, timestamp);
                        })
                        .collect(Collectors.toList()));
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiError> onRequestValidationException(RequestValidationException e) {
        String message = e.getMessage();
        log.error("RequestValidationException: {}", message);

        String reason = "Incorrectly made request.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("BAD_REQUEST", reason, message, timestamp));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> onMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = e.getMessage();
        log.error("MissingServletRequestParameterException: {}", message);

        String reason = "Incorrectly made request.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("BAD_REQUEST", reason, message, timestamp));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> onDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage();
        log.error("DataIntegrityViolationException: {}", message);

        String reason = "Integrity constraint has been violated.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError("CONFLICT", reason, message, timestamp));
    }

    @ExceptionHandler(EwmStatsServerException.class)
    public ResponseEntity<ApiError> onEwmStatsServerException(EwmStatsServerException e) {
        String message = e.getMessage();
        log.error("EwmStatsServerException: {}", message);

        String reason = "Exception on statistics server detected.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", reason, message, timestamp));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> onThrowable(Throwable e) {
        String message = e.getMessage();
        log.error("Unpredictable error: {}", message);

        String reason = "Unpredictable error on server detected.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", reason, message, timestamp));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiError> onObjectNotFoundException(ObjectNotFoundException e) {
        String message = String.format("%s with id=%s was not found", e.getObjectType(), e.getObjectId());
        log.error("ObjectNotFoundException: {}", message);

        String reason = "The required object was not found.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", reason, message, timestamp));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = e.getMessage();
        log.error("MethodArgumentTypeMismatchException: {}", message);

        String reason = "Incorrectly made request.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("BAD_REQUEST", reason, message, timestamp));
    }

    @ExceptionHandler(ViolationOperationRulesException.class)
    public ResponseEntity<ApiError> onViolationOperationRulesException(ViolationOperationRulesException e) {
        String message = e.getMessage();
        log.error("ViolationOperationRulesException: {}", message);

        String reason = "For the requested operation the conditions are not met.";
        String timestamp = LocalDateTime.now().format(timeFormatter);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError("CONFLICT", reason, message, timestamp));
    }

    private static String getFieldName(ConstraintViolation constraintViolation) {
        String[] propertyPath = constraintViolation.getPropertyPath().toString().split("\\.");
        return propertyPath[propertyPath.length - 1];
    }
}
