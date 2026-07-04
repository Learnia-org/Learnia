package com.proyecto.Learnia.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleValidatio(ResourceNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message" , ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public  ResponseEntity<?> handleValidation(ConstraintViolationException ex){
        String msg = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", msg));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadJson(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = "Json Invalido o incorrecto";
        if (cause instanceof IllegalArgumentException) {
            message = cause.getMessage();
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", ex.getMessage(),
                        "status", HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(SuccesException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> handleSuccess(SuccesException ex) {
        return org.springframework.http.ResponseEntity.ok(java.util.Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRole(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Validación de Enum fallida");
        response.put("mensaje", ex.getMessage()); // Aquí saldrá tu mensaje personalizado
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBodyValidation(MethodArgumentNotValidException ex) {

        List<String> mensajes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", mensajes));
    }
}
