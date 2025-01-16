package com.example.country.service;

import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CountryService {

    Country add(Country country);

    Optional<Country> findById(Long id);

    boolean isCountryExistsByNameOrCode(String name, String code);

    boolean isCountryExistsByNameOrCodeAndNotId(String name, String code, Long id);

    Page<Country> findAll(CountryFilter countryFilter, Pageable pageable);

    Country update(Country oldCountry, Country newCountry);

    void delete(Country country);
}
