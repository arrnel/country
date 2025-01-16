package com.example.country;

import org.springframework.boot.SpringApplication;

public class TestCountryApplication {

    public static void main(String[] args) {
        SpringApplication.from(CountryApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
