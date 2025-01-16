package com.example.country.data.controller.advice;

import com.example.country.ex.CountryNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryNotFoundControllerAdvice: Module test")
class CountryNotFoundControllerAdviceTest {

    private static final String API_VERSION = "1.0";
    private static final String REQUEST_URI = "/api/v1/country/1";
    private static final String MESSAGE = "Country not found";

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CountryNotFoundControllerAdvice advice;

    @Test
    @DisplayName("handleCountryNotFoundException: returns NOT_FOUND")
    void handleCountryNotFoundException_ReturnsNotFound() {

        // Data
        final var reason = "Country with id = [1] not found";
        final var exception = new CountryNotFoundException(reason);

        // Mock
        ReflectionTestUtils.setField(advice, "apiVersion", API_VERSION);
        Mockito.doReturn(REQUEST_URI)
                .when(request)
                .getRequestURI();

        // Steps
        final var result = advice.handleCountryNotFoundException(exception, request);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(MESSAGE, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(REQUEST_URI, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

}
