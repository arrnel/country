package com.example.country.data.controller;

import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.UpdateCountryRequestDTO;
import com.example.country.data.entity.Country;
import com.example.country.ex.CountryAlreadyExistsException;
import com.example.country.ex.CountryNotFoundException;
import com.example.country.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryController: Module tests")
class CountryControllerTest {

    static final String VALID_COUNTRY_NAME = "Japan";
    static final String VALID_COUNTRY_CODE = "JP";
    static final String VALID_UPDATED_COUNTRY_NAME = "United Arab Emirates";
    static final String VALID_UPDATED_COUNTRY_CODE = "UAE";
    static final String SHORT_COUNTRY_NAME = "La";
    static final String SHORT_COUNTRY_CODE = "J";

    @Mock
    CountryService countryService;

    @InjectMocks
    CountryController countryController;

    @Test
    @DisplayName("country: returns country when request is valid")
    void country_ValidRequest_ReturnsCountry() {

        // Data
        final var country = new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);

        // Mock
        Mockito.doReturn(Optional.of(country))
                .when(countryService)
                .findById(country.getId());

        // Steps
        var result = countryController.country(country.getId());

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(country, result)
        );

        Mockito.verify(countryService).findById(country.getId());
        Mockito.verifyNoMoreInteractions(countryService);

    }

    @Test
    @DisplayName("country: throws CountryNotFoundException when country not found")
    void country_ValidRequest_ThrowsCountryNotFound() {

        // Data
        final var country = new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);

        // Mock
        Mockito.doReturn(Optional.empty())
                .when(countryService)
                .findById(country.getId());

        // Steps
        assertThrows(CountryNotFoundException.class, () -> countryController.country(1L));

        // Assertions
        Mockito.verify(countryService).findById(country.getId());
        Mockito.verifyNoMoreInteractions(countryService);

    }

    @Test
    @DisplayName("findById: returns country when request is valid")
    void findById_ValidRequest_ReturnsCountry() {

        // Data
        final var country = new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);
        final var expectedResponseDTO = new CountryResponseDTO(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);

        // Test
        final var result = countryController.findById(country);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponseDTO, result.getBody()),
                () -> assertEquals(HttpStatus.OK, result.getStatusCode())
        );

        Mockito.verifyNoMoreInteractions(countryService);

    }

    @Test
    @DisplayName("update: updates country when request is valid")
    void update_ValidRequest_UpdatesCountry() throws BindException {

        // Data
        final var oldCountry = new Country(1L, VALID_COUNTRY_CODE, VALID_COUNTRY_CODE);
        final var requestDTO = new UpdateCountryRequestDTO(VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE);
        final var updatedCountry = new Country(1L, VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE);
        final var responseDTO = new CountryResponseDTO(1L, VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE);
        final var bindingResult = new MapBindingResult(Map.of(), "request");

        // Mock
        doReturn(false)
                .when(countryService)
                .isCountryExistsByNameOrCodeAndNotId(VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE, 1L);
        doReturn(updatedCountry)
                .when(countryService)
                .update(any(Country.class), any(Country.class));

        // Steps
        var result = countryController.update(oldCountry, requestDTO, bindingResult);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(responseDTO, result.getBody()),
                () -> assertEquals(HttpStatus.OK, result.getStatusCode())
        );

        Mockito.verify(countryService).isCountryExistsByNameOrCodeAndNotId(VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE, 1L);
        Mockito.verify(countryService).update(any(Country.class), any(Country.class));
        Mockito.verifyNoMoreInteractions(countryService);
    }


    @Test
    @DisplayName("update: throws CountryAlreadyExistsException when country already exists")
    void update_CountryAlreadyExists_ThrowsConflict() {

        // Data
        final var oldCountry = new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);
        final var requestDTO = new UpdateCountryRequestDTO(VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE);
        final var bindingResult = new MapBindingResult(Map.of(), "request");

        // Mock
        Mockito.doReturn(true)
                .when(countryService)
                .isCountryExistsByNameOrCodeAndNotId(VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE, 1L);

        // Steps
        final var exception = assertThrows(CountryAlreadyExistsException.class, () ->
                countryController.update(oldCountry, requestDTO, bindingResult));

        // Assertions
        assertEquals("Country already exists", exception.getMessage());

        Mockito.verify(countryService).isCountryExistsByNameOrCodeAndNotId(VALID_UPDATED_COUNTRY_NAME, VALID_UPDATED_COUNTRY_CODE, 1L);
        Mockito.verifyNoMoreInteractions(countryService);

    }


    @Test
    @DisplayName("update: throws BindException when request is invalid")
    void update_InvalidRequest_ThrowsBindException() {
        // Data
        final var oldCountry = new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);
        final var requestDTO = new UpdateCountryRequestDTO(SHORT_COUNTRY_NAME, SHORT_COUNTRY_CODE);
        final var bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "name", "error"));
        bindingResult.addError(new FieldError("request", "code", "error"));

        // Steps
        final var exception = assertThrows(BindException.class, () ->
                countryController.update(oldCountry, requestDTO, bindingResult));

        // Assertions
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(
                        List.of(new FieldError("request", "name", "error"),
                                new FieldError("request", "code", "error")),
                        exception.getAllErrors())
        );

        Mockito.verifyNoInteractions(countryService);

    }

    @Test
    @DisplayName("delete: returns NO_CONTENT when request is valid")
    void delete_ValidRequest_ReturnsNoContent() {

        // Data
        final var country = new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);

        // Steps
        final var result = countryController.delete(country);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode())
        );

        Mockito.verify(countryService).delete(country);
        Mockito.verifyNoMoreInteractions(countryService);

    }


}
