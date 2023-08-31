package dev.strongtino.soteria.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm a");

    public static String formatDate(long millis) {
        return dateFormat.format(new Date(millis));
    }

    public static String formatDuration(long millis) {
        if (millis == Long.MAX_VALUE) return "Permanent";

        millis += 1;

        long seconds = millis / 1000L;
        if (seconds < 60L) return seconds + " second" + (seconds == 1 ? "" : 's');

        long minutes = seconds / 60L;
        if (minutes < 60L) return minutes + " minute" + (minutes == 1 ? "" : 's');

        long hours = minutes / 60L;
        if (hours < 24L) return hours + " hour" + (hours == 1 ? "" : 's');

        long days = hours / 24L;
        if (days < 30L) return days + " day" + (days == 1 ? "" : 's');

        long months = days / 30L;
        if (months < 12) return months + " month" + (months == 1 ? "" : 's');

        long years = months / 12L;
        return years + " year" + (years == 1 ? "" : 's');
    }

    public static long getDuration(String input) {
        input = input.toLowerCase();

        if (Character.isLetter(input.charAt(0))) {
            return Long.MAX_VALUE;
        }
        long result = 0L;

        StringBuilder number = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                String str = number.toString();

                if (Character.isLetter(c) && !str.isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }

    private static long convert(int value, char charType) {
        switch (charType) {
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'w':
                return value * TimeUnit.DAYS.toMillis(7L);
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
            default:
                return -1L;
        }
    }
}
