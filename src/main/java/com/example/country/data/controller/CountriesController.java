package com.example.country.data.controller;

import com.example.country.data.controller.dto.AddCountryRequestDTO;
import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.PageResponseDTO;
import com.example.country.ex.CountryAlreadyExistsException;
import com.example.country.mapper.CountryMapper;
import com.example.country.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class CountriesController {

    private final CountryService countryService;

    @PostMapping
    public ResponseEntity<CountryResponseDTO> add(@Valid @RequestBody AddCountryRequestDTO requestDTO,
                                                  BindingResult bindingResult
    ) throws BindException, CountryAlreadyExistsException {

        if (bindingResult.hasErrors())
            throw bindingResult instanceof BindException exception
                    ? exception
                    : new BindException(bindingResult);

        if (countryService.isCountryExistsByNameOrCode(requestDTO.name(), requestDTO.code()))
            throw new CountryAlreadyExistsException("Country already exists");

        return new ResponseEntity<>(
                CountryMapper.toDTO(countryService.add(CountryMapper.fromCreateDTO(requestDTO))),
                HttpStatus.CREATED
        );

    }

    @GetMapping
    public ResponseEntity<PageResponseDTO> findAll(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "code", required = false) String code,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(
                CountryMapper.toPage(
                        countryService.findAll(
                                new CountryFilter(name, code),
                                pageable)));
    }

}
