package com.example.country.data.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record PageResponseDTO(

        @JsonProperty("current_page")
        Integer currentPage,

        @JsonProperty("pages")
        Integer totalPages,

        @JsonProperty("items_per_page")
        Integer itemsPerPage,

        @JsonProperty("total_items")
        Long totalItems,

        @JsonProperty("data")
        List<?> data

) implements Serializable {

    @Builder
    @JsonCreator
    public PageResponseDTO {

        if (currentPage == null)
            currentPage = 0;

        if (totalPages == null)
            totalPages = 0;

        if (itemsPerPage == null)
            itemsPerPage = 0;

        if (totalItems == null)
            totalItems = 0L;

        if (data == null)
            data = Collections.emptyList();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageResponseDTO that = (PageResponseDTO) o;
        return Objects.equals(data, that.data) && Objects.equals(totalItems, that.totalItems) && Objects.equals(totalPages, that.totalPages) && Objects.equals(currentPage, that.currentPage) && Objects.equals(itemsPerPage, that.itemsPerPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPage, totalPages, itemsPerPage, totalItems, data);
    }

}
