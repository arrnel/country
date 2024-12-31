package com.example.country.specs.filters;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartialTextSpec {

    public void specify(
            @Nonnull String fieldName,
            @Nullable String text,
            @Nonnull Root<?> root,
            @Nonnull CriteriaBuilder builder,
            @Nonnull List<Predicate> predicates
    ) {
        if (text != null && !text.trim().isEmpty())
            predicates.add(
                    builder.like(
                            builder.lower(
                                    root.get(fieldName)),
                            "%" + text.toLowerCase() + "%"));
    }

}
