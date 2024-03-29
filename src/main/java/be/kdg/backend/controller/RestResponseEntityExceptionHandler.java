package be.kdg.backend.controller;

import be.kdg.backend.exception.AuthenticationException;
import be.kdg.backend.exception.NotUniqueException;
import be.kdg.backend.exception.ResourceNotFoundException;
import be.kdg.backend.exception.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {IllegalStateException.class, NotUniqueException.class})
    protected ResponseEntity<RestErrorResponse> handleConflict(RuntimeException ex) {
        var responseBody = new RestErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<RestErrorResponse> handleBadRequest(RuntimeException ex) {
        var responseBody = new RestErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<RestErrorResponse> handleNotFound(RuntimeException ex) {
        var responseBody = new RestErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }


    @ExceptionHandler(value = {AuthenticationException.class, RuntimeException.class})
    protected ResponseEntity<RestErrorResponse> handleInternalError(RuntimeException ex) {
        var responseBody = new RestErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
}
