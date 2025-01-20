package com.example.country.mapper;

import com.example.country.data.controller.dto.AddCountryRequestDTO;
import com.example.country.data.controller.dto.CountryResponseDTO;
import com.example.country.data.controller.dto.PageResponseDTO;
import com.example.country.data.controller.dto.UpdateCountryRequestDTO;
import com.example.country.data.entity.Country;
import org.springframework.data.domain.Page;

public class CountryMapper {

    private CountryMapper() {
    }

    public static Country fromCreateDTO(AddCountryRequestDTO requestDTO) {
        return Country.builder()
                .name(requestDTO.name())
                .code(requestDTO.code())
                .build();
    }

    public static Country fromUpdateDTO(UpdateCountryRequestDTO requestDTO) {
        return Country.builder()
                .name(requestDTO.name())
                .code(requestDTO.code())
                .build();
    }

    public static CountryResponseDTO toDTO(Country entity) {
        return CountryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .dateCreated(entity.getDateCreated().toLocalDateTime())
                .dateUpdated(entity.getDateUpdated().toLocalDateTime())
                .build();
    }

    public static PageResponseDTO toPage(Page<Country> page) {
        return PageResponseDTO.builder()
                .currentPage(page.getPageable().getPageNumber())
                .itemsPerPage(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .data(page.getContent().stream().map(CountryMapper::toDTO).toList())
                .build();
    }

    public static Country update(Country oldCountry, Country newCountry) {
        return Country.builder()
                .id(oldCountry.getId())
                .name(newCountry.getName() == null || newCountry.getName().isEmpty()
                        ? oldCountry.getName()
                        : newCountry.getName())
                .code(newCountry.getCode() == null || newCountry.getCode().isEmpty()
                        ? oldCountry.getCode()
                        : newCountry.getCode())
                .dateCreated(oldCountry.getDateCreated())
                .build();
    }

}
