package com.example.country.data.controller.graphql;

import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.PageResponseDTO;
import com.example.country.ex.CountryNotFoundException;
import com.example.country.mapper.CountryMapper;
import com.example.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CountryQueryController {

    private final CountryService countryService;

    @QueryMapping
    public CountryResponseDTO findById(@Argument Long id) {
        return CountryMapper.toDTO(
                countryService.findById(id)
                        .orElseThrow(() -> new CountryNotFoundException("Country with id = [%d] not found".formatted(id))));
    }

    @QueryMapping
    public PageResponseDTO findAll(
            @Argument String name,
            @Argument String code,
            @Argument @DefaultValue(value = "0") int page,
            @Argument @DefaultValue(value = "10") int size,
            @Argument @DefaultValue(value = "id") String sort
    ) {
        return CountryMapper.toPage(
                countryService.findAll(
                        new CountryFilter(name, code),
                        PageRequest.of(page, size, Sort.by(sort))));
    }

}
