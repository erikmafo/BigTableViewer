package com.erikmafo.btviewer.sql.convert.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateTimeFormatUtil {
    private static final String DATE = "uuuu-MM-dd";
    private static final String DATE_HOUR = "uuuu-MM-dd HH";
    private static final String DATE_HOUR_MINUTE = "uuuu-MM-dd HH:mm";
    private static final String DATE_HOUR_MINUTE_SECOND = "uuuu-MM-dd HH:mm:ss";
    private static final String DATE_HOUR_MINUTE_SECOND_MILLIS = "uuuu-MM-dd HH:mm:ss:SS";
    private static final LocalTime MIDNIGHT_LOCAL_TIME = LocalTime.of(0, 0, 0);

    /**
     * Converts a date time string to the number of microseconds since 1970-01-01T00:00:00Z.
     * @param dateTime - a date time string.
     * @return number of microseconds since 1970-01-01T00:00:00Z.
     */
    public static long toMicros(@NotNull String dateTime) {
        return TimeUnit.MILLISECONDS.toMicros(toEpochMilli(dateTime));
    }

    private static long toEpochMilli(@NotNull String dateTime) {
        return toMillis(dateTime, getDateFormat(dateTime));
    }

    @NotNull
    private static String getDateFormat(@NotNull String dateTime) {
        String format;
        if (dateTime.length() == DATE.length()) {
            format = DATE;
        } else if (dateTime.length() == DATE_HOUR.length()) {
            format = DATE_HOUR;
        } else if (dateTime.length() == DATE_HOUR_MINUTE.length()) {
            format = DATE_HOUR_MINUTE;
        } else if (dateTime.length() == DATE_HOUR_MINUTE_SECOND.length()) {
            format = DATE_HOUR_MINUTE_SECOND;
        }  else if (dateTime.length() == DATE_HOUR_MINUTE_SECOND_MILLIS.length()) {
            format = DATE_HOUR_MINUTE_SECOND_MILLIS;
        } else {
            throw new IllegalArgumentException(getErrorMessage(dateTime));
        }
        return format;
    }

    private static long toMillis(@NotNull String dateTime, String format) {
        var trimmedDateTime = dateTime.trim();
        var offset = ZoneOffset.ofHours(0);
        var formatter = DateTimeFormatter.ofPattern(format);
        var dt = isDate(format)
                ? LocalDateTime.of(LocalDate.parse(trimmedDateTime, formatter), MIDNIGHT_LOCAL_TIME)
                : LocalDateTime.parse(trimmedDateTime, formatter);
        return dt.toInstant(offset).toEpochMilli();
    }

    private static String getErrorMessage(@NotNull String dateTime) {
        return String.format("Could not parse %s as date. Supported formats are: \n%s",
                dateTime, String.join("\n", supportedFormats()));
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private static List<String> supportedFormats() {
        return Arrays.asList(
                DATE,
                DATE_HOUR,
                DATE_HOUR_MINUTE,
                DATE_HOUR_MINUTE_SECOND,
                DATE_HOUR_MINUTE_SECOND_MILLIS
        );
    }

    private static boolean isDate(String format) {
        return DATE.equals(format);
    }
}
