package com.example.country.util;

import com.google.protobuf.util.Timestamps;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {

    private DateUtil(){}

    public static Timestamp getCurrentTimestamp(){
        return Timestamp.valueOf(LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS));
    }

    public static com.google.protobuf.Timestamp timestampToGrpcDate(Timestamp timestamp){
        return Timestamps.fromDate(new Date(timestamp.getTime()));
    }

}
