package com.example.country.data.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional // using for remove data from db after test
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CountryController: Integration tests")
class CountryControllerIT {

    private static final String COUNTRY_URL = "/api/v1/country/";

    @Autowired
    MockMvc mockMvc;


    @Test
    @Sql("/sql/countries.sql")
    @DisplayName("findById: returns country when request is valid")
    void findById_ValidRequest_ReturnsCountry() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.get(COUNTRY_URL + 1);

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                	"id": 1,
                                	"name": "Japan",
                                	"code": "JP"
                                }
                                """)
                );

    }

    @Test
    @Sql("/sql/countries.sql")
    @DisplayName("update: updates country when request is valid")
    void update_ValidRequest_UpdatesCountry() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.patch(COUNTRY_URL + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Spain",
                          "code": "SP"
                        }""");

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                	"id": 1,
                                	"name": "Spain",
                                	"code": "SP"
                                }
                                """)
                );

    }

    @Test
    @DisplayName("update: returns NOT_FOUND when country does not exist")
    void update_NotFound_ReturnsNotFound() throws Exception {

        // Data
        final var countryId = 1;
        final var requestBuilder = MockMvcRequestBuilders.patch(COUNTRY_URL + countryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "France",
                          "code": "DE"
                        }""");

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                	"apiVersion": "1.0",
                                	"error": {
                                		"code": "404 NOT_FOUND",
                                		"message": "Country not found",
                                		"errors": [
                                			{
                                				"domain": "/api/v1/country/1",
                                				"reason": "Country with id = [%d] not found",
                                				"message": "Country not found"
                                			}
                                		]
                                	}
                                }
                                """.formatted(countryId))
                );

    }

    @Test
    @Sql("/sql/countries.sql")
    @DisplayName("update: throws CONFLICT when country already exists")
    void update_ExistingCountry_ThrowsConflict() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.patch(COUNTRY_URL + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "France",
                          "code": "DE"
                        }""");

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                	"apiVersion": "1.0",
                                	"error": {
                                		"code": "409 CONFLICT",
                                		"message": "Country already exists",
                                		"errors": [
                                			{
                                				"domain": "/api/v1/country/1",
                                				"reason": "Country already exists",
                                				"message": "Country already exists"
                                			}
                                		]
                                	}
                                }
                                """)
                );

    }


    @Test
    @Sql("/sql/countries.sql")
    @DisplayName("update: return BAD_REQUEST when request is invalid")
    void update_InvalidRequest_ThrowsBindException() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.patch(COUNTRY_URL + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Fe",
                          "code": "D"
                        }""");

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                 	"apiVersion": "1.0",
                                 	"error": {
                                 		"code": "400 BAD_REQUEST",
                                 		"message": "Bad request. Multiple validation errors",
                                 		"errors": [
                                 			{
                                 				"domain": "/api/v1/country/1",
                                 				"reason": "Size",
                                 				"message": "invalid country code"
                                 			},
                                 			{
                                 				"domain": "/api/v1/country/1",
                                 				"reason": "Size",
                                 				"message": "invalid country name"
                                 			}
                                 		]
                                 	}
                                 }
                                """)
                );
    }

    @Test
    @Sql("/sql/countries.sql")
    @DisplayName("delete: returns NO_CONTENT when country is deleted")
    void delete_ReturnsNoContent() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.delete(COUNTRY_URL + 1);

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @DisplayName("delete: returns NOT_FOUND when country does not exist")
    void delete_ReturnsNotFound() throws Exception {

        // Data
        final var countryId = 1;
        final var requestBuilder = MockMvcRequestBuilders.delete(COUNTRY_URL + countryId);

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  	"apiVersion": "1.0",
                                  	"error": {
                                  		"code": "404 NOT_FOUND",
                                  		"message": "Country not found",
                                  		"errors": [
                                  			{
                                  				"domain": "/api/v1/country/1",
                                  				"reason": "Country with id = [%d] not found",
                                  				"message": "Country not found"
                                  			}
                                  		]
                                  	}
                                  }
                                """.formatted(countryId))
                );
    }

}
