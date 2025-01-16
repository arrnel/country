package com.example.country.data.controller.advice;

import com.example.country.data.controller.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class BadRequestControllerAdvice {

    @Value("${app.api.version}")
    private String apiVersion;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException exception,
                                                        HttpServletRequest request
    ) {

        log.info("Bad request. uri: {}, message: {}", request.getRequestURI(), exception.getMessage());

        var errorItems = exception.getAllErrors().stream()
                .map(error -> new ApiError.ErrorItem(
                        request.getRequestURI(),
                        error.getCode(),
                        error.getDefaultMessage()))
                .toList();

        var message = errorItems.size() > 1
                ? "Bad request. Multiple validation errors"
                : "Bad request. " + errorItems.getFirst().itemMessage();

        ApiError apiError = ApiError.builderErrors()
                .apiVersion(apiVersion)
                .code(HttpStatus.BAD_REQUEST.toString())
                .message(message)
                .errorItems(errorItems)
                .buildErrors();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);

    }


}
