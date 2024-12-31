package com.example.country.data.controller.advice;

import com.example.country.ex.CountryAlreadyExistsException;
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
@DisplayName("CountryAlreadyExistsControllerAdvice: Module test")
class CountryAlreadyExistsControllerAdviceTest {

    private static final String API_VERSION = "1.0";
    private static final String ERROR_MESSAGE = "Country already exists";
    private static final String REQUEST_URI = "/api/v1/countries";

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CountryAlreadyExistsControllerAdvice advice;

    @Test
    @DisplayName("handleCountryNotFoundException: returns CONFLICT")
    void handleCountryNotFoundException_ReturnsNotFound_Test() {

        // Data
        final var reason = "Country with name = [Japan] already exists";
        final var exception = new CountryAlreadyExistsException(reason);

        // Mock
        ReflectionTestUtils.setField(advice, "apiVersion", API_VERSION);
        Mockito.doReturn(REQUEST_URI)
                .when(request)
                .getRequestURI();

        // Steps
        final var result = advice.handeCountryAlreadyExists(exception, request);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.CONFLICT, result.getStatusCode()),
                () -> assertEquals(API_VERSION, result.getBody().getApiVersion()),
                () -> assertEquals(ERROR_MESSAGE, result.getBody().getError().errors().getFirst().itemMessage()),
                () -> assertEquals(REQUEST_URI, result.getBody().getError().errors().getFirst().domain()),
                () -> assertEquals(reason, result.getBody().getError().errors().getFirst().reason())
        );

        verifyNoMoreInteractions(request);

    }

}
