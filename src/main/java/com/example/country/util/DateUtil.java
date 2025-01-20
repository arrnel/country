package com.example.country.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private DateUtil(){}

    public static Timestamp getCurrentTimestamp(){
        return Timestamp.valueOf(LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS));
    }

}
