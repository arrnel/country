package com.example.country.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.from(ZoneOffset.UTC));

    @Override
    public void serialize(LocalDateTime ldt, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(FORMATTER.format(ldt));
    }
}
