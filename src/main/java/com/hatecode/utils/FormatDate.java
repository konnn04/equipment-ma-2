package com.hatecode.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatDate {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond());
    }

    public static LocalDate toLocalDate(LocalDateTime LocalDateTime) {
        if (LocalDateTime == null) {
            return null;
        }
        return LocalDateTime.toLocalDate();
    }

    public static LocalDateTime combineDateAndTime(LocalDate date, String time) {
        if (date == null || time == null || time.isEmpty()) {
            return null;
        }
        String[] timeParts = time.split(":");
        if (timeParts.length != 2) {
            throw new IllegalArgumentException("Time must be in the format HH:mm");
        }
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        int second = 0;
        return LocalDateTime.of(date, LocalDateTime.of(0, 1, 1, hour, minute, second).toLocalTime());
    }

    public static String formatDate(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }
        return localDate.format(DATE_FORMATTER);
    }
}
