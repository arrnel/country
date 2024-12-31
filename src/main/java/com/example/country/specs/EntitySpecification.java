package com.example.country.specs;

public interface EntitySpecification<D, S> {
    D findByCriteria(S source);
}
