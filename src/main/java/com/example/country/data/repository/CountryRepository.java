package com.example.country.data.repository;

import com.example.country.data.entity.Country;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CountryRepository extends CrudRepository<Country, Long>, JpaSpecificationExecutor<Country> {

    Optional<Country> findById(@Nonnull Long id);

    @Query("SELECT COUNT (c) > 0 FROM Country c WHERE lower(c.name) = lower(:name) or lower(c.code) = lower(:code)")
    boolean existsByNameOrCode(@Nonnull String name, @Nonnull String code);

    @Query("SELECT COUNT(c) > 0 FROM Country c WHERE (lower(c.name) = lower(:name) OR lower(c.code) = lower(:code)) AND c.id != :id")
//    @Query("SELECT COUNT(c) > 0 FROM Country c WHERE (lower(c.name) = lower(:name) AND c.id != :id) OR (lower(c.code) = lower(:code) AND c.id != :id)")
    boolean existsByNameOrCodeAndIdNot(@Nonnull String name, @Nonnull String code, @Nonnull Long id);

}
