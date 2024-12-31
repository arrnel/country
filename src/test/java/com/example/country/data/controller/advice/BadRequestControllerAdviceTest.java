package com.example.country.data.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryAlreadyExistsControllerAdvice: Module test")
class BadRequestControllerAdviceTest {

    private static final String API_VERSION = "1.0";
    private static final String MULTIPLE_VALIDATION_ERRORS = "Bad request. Multiple validation errors";
    private static final String COUNTRIES_REQUEST_URL = "/api/v1/countries";
    private static final String INVALID_COUNTRY_NAME = "invalid country name";
    private static final String INVALID_COUNTRY_CODE = "invalid country code";

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private BadRequestControllerAdvice advice;

    @Test
    @DisplayName("handleBindException: returns BAD_REQUEST")
    void handleBindException_ReturnsBadRequest_Test() {

        // Data
        final var bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "name", INVALID_COUNTRY_NAME));
        bindingResult.addError(new FieldError("request", "code", INVALID_COUNTRY_CODE));
        final var exception = new BindException(bindingResult);

        // Mock
        ReflectionTestUtils.setField(advice, "apiVersion", API_VERSION);
        Mockito.doReturn(COUNTRIES_REQUEST_URL)
                .when(request)
                .getRequestURI();

        // Steps
        final var result = advice.handleBindException(exception, request);

        // Assertions
        final var responseBody = result.getBody();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()),
                () -> assertEquals(API_VERSION, responseBody.getApiVersion()),
                () -> assertEquals(MULTIPLE_VALIDATION_ERRORS, responseBody.getError().message()),
                () -> assertEquals(COUNTRIES_REQUEST_URL, responseBody.getError().errors().getFirst().domain()),
                () -> assertEquals(INVALID_COUNTRY_NAME, responseBody.getError().errors().getFirst().itemMessage()),
                () -> assertEquals(INVALID_COUNTRY_CODE, responseBody.getError().errors().getLast().itemMessage())
        );

        verifyNoMoreInteractions(request);

    }

}
