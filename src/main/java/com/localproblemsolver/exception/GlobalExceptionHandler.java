package com.localproblemsolver.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // VALIDATION EXCEPTION
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Validation failed");
        response.put("errors", errors);

        return response;
    }


    // =========================================================
    // PROBLEM NOT FOUND
    // =========================================================

    @ExceptionHandler(ProblemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleProblemNotFoundException(
            ProblemNotFoundException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return response;
    }


    // =========================================================
    // INVALID STATUS TRANSITION
    // =========================================================

    @ExceptionHandler(InvalidStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidStatusTransitionException(
            InvalidStatusTransitionException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return response;
    }


    // =========================================================
    // CATEGORY NOT FOUND
    // =========================================================

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleCategoryNotFoundException(
            CategoryNotFoundException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return response;
    }


    // =========================================================
    // ACCESS DENIED
    // =========================================================

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDeniedException(
            AccessDeniedException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                "You are not authorized to access this resource"
        );

        return response;
    }


    // =========================================================
    // RESPONSE STATUS EXCEPTION
    // =========================================================

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                exception.getReason() != null
                        ? exception.getReason()
                        : "Request failed"
        );

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(response);
    }


    // =========================================================
    // INVALID ARGUMENT
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return response;
    }


    // =========================================================
    // DATABASE / DATA ACCESS EXCEPTION
    // =========================================================

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> handleDatabaseException(
            DataAccessException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                "Database service is currently unavailable"
        );

        return response;
    }


    // =========================================================
    // GENERIC / UNEXPECTED EXCEPTION
    // =========================================================

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGenericException(
            Exception exception) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                "An unexpected error occurred"
        );

        return response;
    }
}