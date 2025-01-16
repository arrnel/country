package com.example.country.data.repository;

import com.example.country.data.controller.dto.CountryFilter;
import com.example.country.data.entity.Country;
import com.example.country.specs.CountrySpecs;
import com.example.country.specs.filters.PartialTextSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        CountrySpecs.class,
        PartialTextSpec.class
})
class CountryRepositoryIT {

    private static final Long CORRECT_ID = 1L;
    private static final String EXISTS_NAME = "Japan";
    private static final String EXISTS_CODE = "JP";

    private static final Long WRONG_ID = 2L;
    private static final String NOT_EXISTS_NAME = "Spain";
    private static final String NOT_EXISTS_CODE = "SP";

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    CountrySpecs countrySpecs;

    @ParameterizedTest(name = "Case {index}: {0}")
    @MethodSource("findAll_ArgumentsProvider")
    @Sql("/sql/countries.sql")
    @DisplayName("findAll: filters by partial name or code")
    void findAll_ByPartialFilters_Test(
            String caseName,
            CountryFilter filter,
            int expectedTotalElements,
            int expectedTotalPages,
            List<String> expectedNames) {

        // Data
        final var pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
        final var specs = countrySpecs.findByCriteria(filter);

        // Steps
        var result = countryRepository.findAll(specs, pageable);

        // Assertions
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(0, result.getPageable().getPageNumber()),
                () -> assertEquals(10, result.getPageable().getPageSize()),
                () -> assertEquals(expectedTotalElements, result.getTotalElements()),
                () -> assertEquals(expectedTotalPages, result.getTotalPages()),
                () -> assertEquals(expectedNames.size(), result.getContent().size()),
                () -> assertTrue(result.getContent().stream()
                        .map(Country::getName)
                        .allMatch(expectedNames::contains))
        );
    }

    static Stream<Arguments> findAll_ArgumentsProvider() {
        return Stream.of(
                Arguments.of(
                        "No filters",
                        new CountryFilter(null, null),
                        4,
                        1,
                        List.of("Japan", "France", "Australia", "Austria")),
                Arguments.of(
                        "Filter by partial name",
                        new CountryFilter("an", null),
                        2,
                        1,
                        List.of("Japan", "France")),
                Arguments.of(
                        "Filter by partial code",
                        new CountryFilter(null, "A"),
                        2,
                        1,
                        List.of("Australia", "Austria")),
                Arguments.of(
                        "Filter by partial name and code",
                        new CountryFilter("an", "F"),
                        1,
                        1,
                        List.of("France"))
        );
    }

    @Sql("/sql/countries.sql")
    @ParameterizedTest(name = "Case {index}: {0}")
    @MethodSource("existsByNameOrCode_ArgumentsProvider")
    @DisplayName("existsByNameOrCode: checks name and code combinations")
    void existsByNameOrCode_Test(String caseName, String name, String code, boolean expectedResult) {

        // Steps & Assertions
        assertEquals(expectedResult, countryRepository.existsByNameOrCode(name, code));

    }

    static Stream<Arguments> existsByNameOrCode_ArgumentsProvider() {

        return Stream.of(
                Arguments.of("Exists name with exists code", EXISTS_NAME, EXISTS_CODE, true),
                Arguments.of("Exists name with not exists code", EXISTS_NAME, NOT_EXISTS_CODE, true),
                Arguments.of("Not exists name with exists code", NOT_EXISTS_NAME, EXISTS_CODE, true),
                Arguments.of("Not exists name and code", NOT_EXISTS_NAME, NOT_EXISTS_CODE, false)
        );

    }

    @Sql("/sql/countries.sql")
    @ParameterizedTest(name = "Case {index}: {0}")
    @MethodSource("existsByNameOrCodeAndIdNot_ArgumentsProvider")
    @DisplayName("existsByNameOrCodeAndIdNot: checks name, code, and id combinations")
    void existsByNameOrCodeAndIdNot_Test(String caseName, String name, String code, Long id, boolean expectedResult) {

        // Steps & Assertions
        assertEquals(expectedResult, countryRepository.existsByNameOrCodeAndIdNot(name, code, id));

    }

    static Stream<Arguments> existsByNameOrCodeAndIdNot_ArgumentsProvider() {

        return Stream.of(
                Arguments.of("Not exists name and code, wrong id", NOT_EXISTS_NAME, NOT_EXISTS_CODE, WRONG_ID, false),
                Arguments.of("Not exists name and code, correct id", NOT_EXISTS_NAME, NOT_EXISTS_CODE, CORRECT_ID, false),
                Arguments.of("Exists name, not exists code, wrong id", EXISTS_NAME, NOT_EXISTS_CODE, WRONG_ID, true),
                Arguments.of("Exists name, not exists code, correct id", EXISTS_NAME, NOT_EXISTS_CODE, CORRECT_ID, false),
                Arguments.of("Not exists name, exists code, wrong id", NOT_EXISTS_NAME, EXISTS_CODE, WRONG_ID, true),
                Arguments.of("Not exists name, exists code, correct id", NOT_EXISTS_NAME, EXISTS_CODE, CORRECT_ID, false),
                Arguments.of("Exists name, exists code, wrong id", EXISTS_NAME, EXISTS_CODE, WRONG_ID, true),
                Arguments.of("Exists name and code, correct id", EXISTS_NAME, EXISTS_CODE, CORRECT_ID, false)

        );
    }

}
