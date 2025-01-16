package com.example.country.data.controller.graphql;

import com.example.country.data.controller.dto.AddCountryRequestDTO;
import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.UpdateCountryRequestDTO;
import com.example.country.data.entity.Country;
import com.example.country.ex.CountryAlreadyExistsException;
import com.example.country.ex.CountryNotFoundException;
import com.example.country.mapper.CountryMapper;
import com.example.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class CountryMutationController {

    private final CountryService countryService;

    @MutationMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CountryResponseDTO add(@Argument AddCountryRequestDTO input) throws CountryAlreadyExistsException {

        if (countryService.isCountryExistsByNameOrCode(input.name(), input.code()))
            throw new CountryAlreadyExistsException("Country already exists");

        return CountryMapper.toDTO(countryService.add(CountryMapper.fromCreateDTO(input)));

    }

    @MutationMapping
    public CountryResponseDTO update(
            @Argument Long id,
            @Argument UpdateCountryRequestDTO input
    ) {

        Country oldCountry = countryService.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country with id = [%d] not found".formatted(id)));

        if (countryService.isCountryExistsByNameOrCodeAndNotId(input.name(), input.code(), oldCountry.getId())) {
            throw new CountryAlreadyExistsException("Country already exists");
        }

        return CountryMapper.toDTO(
                countryService.update(
                        oldCountry,
                        CountryMapper.fromUpdateDTO(input)
                )
        );

    }

    @MutationMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Argument Long id) {
        countryService.delete(
                countryService.findById(id)
                        .orElseThrow(() -> new CountryNotFoundException("Country with id = [%d] not found".formatted(id))));
    }

}
