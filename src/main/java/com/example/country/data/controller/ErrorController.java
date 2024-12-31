package com.example.country.data.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class ErrorController extends AbstractErrorController {

    public ErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, Collections.emptyList());
//        super(errorAttributes, errorViewResolvers); // REMOVE:
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        return new ResponseEntity<>(
                this.getErrorAttributes(request, ErrorAttributeOptions.defaults()),
                this.getStatus(request)
        );
    }

}
