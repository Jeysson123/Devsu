package com.devsu.backend.application.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*Handle date features*/
@Service
public class DateService {

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String formatToDayMonthYear(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isBlank()) return "";
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, INPUT_FORMAT);
            return dateTime.format(OUTPUT_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(isoDateTime.substring(0, 23),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                return dateTime.format(OUTPUT_FORMAT);
            } catch (Exception ex) {
                return isoDateTime;
            }
        }
    }
}