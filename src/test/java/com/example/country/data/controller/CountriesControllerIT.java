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
@DisplayName("CountriesController: Integration tests")
class CountriesControllerIT {

    private static final String COUNTRIES_URL = "/api/v1/countries";

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("add: returns added country when request is valid")
    void add_ValidRequest_ReturnsAddedCountry() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.post(COUNTRIES_URL)
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
                        status().isCreated(),
                        // header().string(HttpHeaders.LOCATION, "http://localhost/api/v1/country/5"),
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
    @Sql("/sql/countries.sql")
    @DisplayName("add: throws CONFLICT when country already exists")
    void add_ExistingCountry_ThrowsConflict() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.post(COUNTRIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Japan",
                          "code": "JP"
                        }""");

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                """
                                        {
                                        	"apiVersion": "1.0",
                                        	"error": {
                                        		"code": "409 CONFLICT",
                                        		"message": "Country already exists",
                                        		"errors": [
                                        			{
                                        				"domain": "/api/v1/countries",
                                        				"reason": "Country already exists",
                                        				"message": "Country already exists"
                                        			}
                                        		]
                                        	}
                                        }
                                        """
                        )
                );

    }

    @Test
    @DisplayName("add: throws BAD_REQUEST when request is invalid")
    void add_InvalidRequest_ThrowsBadRequest() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.post(COUNTRIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Jp",
                          "code": "J"
                        }""");

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        // Assertions
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                """
                                        {
                                         	"apiVersion": "1.0",
                                         	"error": {
                                         		"code": "400 BAD_REQUEST",
                                         		"message": "Bad request. Multiple validation errors",
                                         		"errors": [
                                         			{
                                         				"domain": "/api/v1/countries",
                                         				"reason": "Size",
                                         				"message": "invalid country code"
                                         			},
                                         			{
                                         				"domain": "/api/v1/countries",
                                         				"reason": "Size",
                                         				"message": "invalid country name"
                                         			}
                                         		]
                                         	}
                                         }
                                        """
                        )
                );

    }

    @Test
    @Sql("/sql/countries.sql")
    @DisplayName("findAll: returns page response for valid request")
    void findAll_ValidRequest_ReturnsPageResponse() throws Exception {

        // Data
        final var requestBuilder = MockMvcRequestBuilders.get(COUNTRIES_URL);

        // Steps
        mockMvc.perform(requestBuilder)
                .andDo(print())

                // Assertions
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                 	"current_page": 0,
                                 	"pages": 1,
                                 	"items_per_page": 10,
                                 	"total_items": 4,
                                 	"data": [
                                 		{
                                 			"id": 1,
                                 			"name": "Japan",
                                 			"code": "JP"
                                 		},
                                 		{
                                 			"id": 2,
                                 			"name": "France",
                                 			"code": "FR"
                                 		},
                                 		{
                                 			"id": 3,
                                 			"name": "Australia",
                                 			"code": "AU"
                                 		},
                                 		{
                                 			"id": 4,
                                 			"name": "Austria",
                                 			"code": "AT"
                                 		}
                                 	]
                                 }
                                """)
                );

    }


}
