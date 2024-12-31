package com.example.country.ex;

public class CountryAlreadyExistsException extends RuntimeException {
    public CountryAlreadyExistsException(String message) {
        super(message);
    }
}
