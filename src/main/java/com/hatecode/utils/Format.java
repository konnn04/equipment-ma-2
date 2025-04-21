package com.hatecode.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {
    public static Date StringToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionMessage.DATE_FORMAT_INVALID);
        }
    }
}
