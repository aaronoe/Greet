package de.aaronoe.greet.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DEFAULT_DATEFORMAT = "EEEE, MMM d";
    private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat(DEFAULT_DATEFORMAT, Locale.ENGLISH);

    public static String convertTimestampToPostDate(long timestamp) {
        return DEFAULT_FORMAT.format(new Date(timestamp));
    }

}
