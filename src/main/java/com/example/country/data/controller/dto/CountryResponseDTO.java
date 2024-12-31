package com.example.country.data.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Objects;

@Builder
public record CountryResponseDTO(

        @JsonProperty("id")
        Long id,

        @JsonProperty("name")
        String name,

        @JsonProperty("code")
        String code

) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryResponseDTO that = (CountryResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code);
    }

}
