package com.penelope.banchanggo.utils;

import android.content.res.Resources;

import com.penelope.banchanggo.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class TimeUtils {

    public static LocalDate getLocalDate(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime getLocalDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String getDateString(long millis) {

        LocalDate date = getLocalDate(millis);
        int year = date.getYear();
        int month = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();

        return String.format(Locale.getDefault(),
                "%d.%02d.%02d",
                year, month, dayOfMonth
        );
    }

    public static String getTimeString(long millis) {

        LocalDateTime ldt = getLocalDateTime(millis);

        int hour = ldt.getHour();
        int minute = ldt.getMinute();
        boolean isMorning = hour < 12;

        return String.format(Locale.getDefault(),
                "%s %d:%02d",
                isMorning ? "오전" : "오후",
                hour % 12,
                minute);
    }

}
