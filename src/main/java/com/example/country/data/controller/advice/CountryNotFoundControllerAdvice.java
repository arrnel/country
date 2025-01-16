package com.example.country.data.controller.advice;

import com.example.country.data.controller.dto.ApiError;
import com.example.country.ex.CountryNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CountryNotFoundControllerAdvice {

    @Value("${app.api.version}")
    private String apiVersion;

    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<ApiError> handleCountryNotFoundException(CountryNotFoundException exception,
                                                                   HttpServletRequest request
    ) {

        log.info("Country not found. uri: {}, message: {}", request.getRequestURI(), exception.getMessage());

        ApiError apiError = ApiError.builder()
                .apiVersion(apiVersion)
                .code(HttpStatus.NOT_FOUND.toString())
                .message("Country not found")
                .domain(request.getRequestURI())
                .reason(exception.getMessage())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);

    }

}
