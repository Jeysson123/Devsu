package com.devsu.backend.web.advice;

import com.devsu.backend.web.dto.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.Collectors;

/**
 * Global exception handler providing uniform API error responses for validation and general exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ResponseWrapper<String> response = new ResponseWrapper<>(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Validación fallida: " + errorMessage
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<String>> handleAllExceptions(Exception ex) {

        System.err.println("Exception caught: " + ex.getMessage());

        String message = "Ocurrió un error inesperado: " + ex.getMessage();

        ResponseWrapper<String> response = new ResponseWrapper<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                message
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}