package com.dtech.taskifypro.exception;

//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
//@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        log.error("Runtime exception occurred: ", ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        log.error("Access denied: ", ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "Access Denied");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        log.error("Unexpected exception occurred: ", ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "An unexpected error occurred");
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return modelAndView;
    }
}
