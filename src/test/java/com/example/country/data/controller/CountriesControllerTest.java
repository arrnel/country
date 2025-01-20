package com.example.country.data.controller;

import com.example.country.data.controller.dto.AddCountryRequestDTO;
import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.PageResponseDTO;
import com.example.country.data.entity.Country;
import com.example.country.ex.CountryAlreadyExistsException;
import com.example.country.mapper.CountryMapper;
import com.example.country.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountriesController: Module tests")
class CountriesControllerTest {

    static final String VALID_COUNTRY_NAME = "Japan";
    static final String SHORT_COUNTRY_NAME = "La";
    static final String VALID_COUNTRY_CODE = "JP";
    static final String SHORT_COUNTRY_CODE = "J";

    @Mock
    CountryService countryService;

    @InjectMocks
    CountriesController countriesController;


    @Test
    @DisplayName("add: returns added country when request is valid")
    void add_ValidRequest_ReturnsAddedCountry() throws BindException {

        // Data
        final var ldtNow = LocalDateTime.now();
        final var now = Timestamp.valueOf(ldtNow);
        final var requestDTO = new AddCountryRequestDTO(VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);
        final var expectedResponseDTO = new CountryResponseDTO(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE, ldtNow, ldtNow);
        final var bindingResult = new MapBindingResult(new HashMap<>(), "request");

        // Mock
        Mockito.doReturn(false)
                .when(countryService)
                .isCountryExistsByNameOrCode(VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);
        Mockito.doReturn(new Country(1L, VALID_COUNTRY_NAME, VALID_COUNTRY_CODE, now, now))
                .when(countryService)
                .add(argThat(countryArg ->
                        countryArg.getName().equals(VALID_COUNTRY_NAME) &&
                                countryArg.getCode().equals(VALID_COUNTRY_CODE) &&
                                countryArg.getId() == null
                ));

        // Test
        final var result = countriesController.add(requestDTO, bindingResult);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponseDTO, result.getBody()),
                () -> assertEquals(HttpStatus.CREATED, result.getStatusCode())
        );

        Mockito.verify(countryService, times(1)).isCountryExistsByNameOrCode(any(String.class), any(String.class));
        Mockito.verify(countryService, times(1)).add(any(Country.class));
        Mockito.verifyNoMoreInteractions(countryService);

    }

    @Test
    @DisplayName("add: throws CountryAlreadyExistsException when country already exists")
    void add_ExistingCountry_ThrowsConflict() throws CountryAlreadyExistsException {

        // Data
        final var requestDTO = new AddCountryRequestDTO(VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);
        final var bindingResult = new MapBindingResult(new HashMap<>(), "request");

        //Mock
        doReturn(true).when(countryService).isCountryExistsByNameOrCode(VALID_COUNTRY_NAME, VALID_COUNTRY_CODE);

        // Steps
        assertThrows(CountryAlreadyExistsException.class, () -> countriesController.add(requestDTO, bindingResult));

        // Assertions
        Mockito.verify(countryService, times(1)).isCountryExistsByNameOrCode(any(String.class), any(String.class));
        Mockito.verifyNoMoreInteractions(countryService);

    }

    @Test
    @DisplayName("add: throws BindException when request is invalid")
    void add_InvalidRequest_ThrowsBindException() throws CountryAlreadyExistsException {

        // Data
        final var requestDTO = new AddCountryRequestDTO(SHORT_COUNTRY_NAME, SHORT_COUNTRY_CODE);
        final var bindingResult = new BindException(new MapBindingResult(Map.of(), "request"));
        bindingResult.addError(new FieldError("request", "name", "error"));
        bindingResult.addError(new FieldError("request", "code", "error"));

        // Steps
        final var exception = assertThrows(BindException.class, () -> countriesController.add(requestDTO, bindingResult));

        // Assertions
        assertEquals(
                List.of(
                        new FieldError("request", "name", "error"),
                        new FieldError("request", "code", "error")),
                exception.getAllErrors());
        Mockito.verifyNoMoreInteractions(countryService);

    }

    @Test
    @DisplayName("findAll: returns page response when request is valid")
    void findAll_ValidRequest_ReturnsPageResponse() {

        // Data
        final var ldtNow = LocalDateTime.now();
        final var now = Timestamp.valueOf(ldtNow);
        final var filter = new CountryFilter("AT", "A");
        final var countries = List.of(
                new Country(1L, "Austria", "AT", now, now),
                new Country(2L, "Australia", "AU", now, now));
        final var pageable = PageRequest.of(0, 20);
        final var page = new PageImpl<>(countries, pageable, countries.size());

        // Mock
        Mockito.doReturn(page)
                .when(countryService)
                .findAll(filter, pageable);

        // Steps
        var result = countriesController.findAll(filter.name(), filter.code(), pageable);

        // Assertions
        assertAll(() -> assertNotNull(result),
                () -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(
                        PageResponseDTO.builder()
                                .currentPage(page.getPageable().getPageNumber())
                                .itemsPerPage(page.getSize())
                                .totalItems(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .data(page.getContent().stream().map(CountryMapper::toDTO).toList())
                                .build(),
                        result.getBody()));

        Mockito.verify(countryService, times(1)).findAll(filter, pageable);
        Mockito.verifyNoMoreInteractions(countryService);


    }

}
