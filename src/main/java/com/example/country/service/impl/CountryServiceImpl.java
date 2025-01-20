package com.example.country.service.impl;

import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.entity.Country;
import com.example.country.data.repository.CountryRepository;
import com.example.country.mapper.CountryMapper;
import com.example.country.service.CountryService;
import com.example.country.specs.CountrySpecs;
import com.example.country.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountrySpecs countrySpecs;

    @Override
    public Country add(Country country) {
        final var now = DateUtil.getCurrentTimestamp();
        return countryRepository.save(
                country.setDateCreated(now)
                        .setDateUpdated(now)
        );
    }

    @Override
    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id);
    }

    @Override
    public boolean isCountryExistsByNameOrCode(String name, String code) {
        return countryRepository.existsByNameOrCode(name, code);
    }

    @Override
    public boolean isCountryExistsByNameOrCodeAndNotId(String name, String code, Long id) {
        return countryRepository.existsByNameOrCodeAndIdNot(name, code, id);
    }

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Page<Country> findAll(CountryFilter countryFilter, Pageable pageable) {
        return countryRepository.findAll(countrySpecs.findByCriteria(countryFilter), pageable);
    }

    @Override
    public Country update(Country oldCountry, Country newCountry) {
        return countryRepository.save(
                CountryMapper.update(oldCountry, newCountry)
                        .setDateUpdated(DateUtil.getCurrentTimestamp()));
    }

    @Override
    public void delete(Country country) {
        countryRepository.delete(country);
    }

}
