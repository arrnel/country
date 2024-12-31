package com.example.country.data.controller.dto;

import lombok.Builder;

import java.util.Objects;

@Builder
public record CountryFilter(

        String name,

        String code

) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryFilter that = (CountryFilter) o;
        return Objects.equals(name, that.name) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }

}
