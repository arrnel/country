package com.example.country.specs;

import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.entity.Country;
import com.example.country.specs.filters.PartialTextSpec;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CountrySpecs implements EntitySpecification<Specification<Country>, CountryFilter> {

    private static final String COUNTRY_NAME = "name";
    private static final String COUNTRY_CODE = "code";

    private final PartialTextSpec partialTextSpec;

    @Override
    public Specification<Country> findByCriteria(CountryFilter source) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            partialTextSpec.specify(COUNTRY_NAME, source.name(), root, builder, predicates);
            partialTextSpec.specify(COUNTRY_CODE, source.code(), root, builder, predicates);

            return builder.and(predicates.toArray(new Predicate[0]));

        };

    }

}
