package com.example.country.data.controller;

import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.UpdateCountryRequestDTO;
import com.example.country.data.entity.Country;
import com.example.country.ex.CountryAlreadyExistsException;
import com.example.country.ex.CountryNotFoundException;
import com.example.country.mapper.CountryMapper;
import com.example.country.service.CountryService;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/country/{id}")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @Nonnull
    @ModelAttribute("country")
    public Country country(@PathVariable("id") Long id) throws CountryNotFoundException {
        return countryService.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country with id = [%d] not found".formatted(id)));
    }

    @GetMapping
    public ResponseEntity<CountryResponseDTO> findById(@ModelAttribute("country") Country country) {
        return ResponseEntity.ok(CountryMapper.toDTO(country));
    }

    @PatchMapping
    public ResponseEntity<CountryResponseDTO> update(
            @ModelAttribute("country") Country oldCountry,
            @Valid @RequestBody UpdateCountryRequestDTO requestDTO,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors())
            throw bindingResult instanceof BindException exception
                    ? exception
                    : new BindException(bindingResult);

        if (countryService.isCountryExistsByNameOrCodeAndNotId(requestDTO.name(), requestDTO.code(), oldCountry.getId()))
            throw new CountryAlreadyExistsException("Country already exists");

        return ResponseEntity.ok(
                CountryMapper.toDTO(
                        countryService.update(
                                oldCountry,
                                CountryMapper.fromUpdateDTO(requestDTO))));

    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@ModelAttribute("country") Country country) {
        countryService.delete(country);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
