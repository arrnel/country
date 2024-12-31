package com.example.country.data.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Objects;

@Builder
public record AddCountryRequestDTO(

        @NotBlank
        @Size(min = 3, max = 50, message = "invalid country name")
        @JsonProperty("name")
        String name,

        @NotBlank
        @Size(min = 2, max = 3, message = "invalid country code")
        @JsonProperty("code")
        String code

) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddCountryRequestDTO that = (AddCountryRequestDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }

}
