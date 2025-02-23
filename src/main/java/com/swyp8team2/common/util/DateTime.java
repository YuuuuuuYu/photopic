package com.swyp8team2.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {

    public static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(formatter);
    }
}
