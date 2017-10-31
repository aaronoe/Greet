package de.aaronoe.greet.utils;


import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.aaronoe.greet.R;

public class DateUtils {

    private static final String DEFAULT_DATEFORMAT = "MMMM d, yyyy";
    private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat(DEFAULT_DATEFORMAT, Locale.ENGLISH);
    private static final DateFormat TODAY_FORMAT = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public static String getGroupItemString(Context context, long timestamp) {
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date(timestamp)); // your date

        Calendar c3 = Calendar.getInstance(); // today

        if (c3.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            if (c3.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
                return TODAY_FORMAT.format(new Date(timestamp));
            } else if (c2.get(Calendar.DAY_OF_YEAR) == c1.get(Calendar.DAY_OF_YEAR)) {
                return context.getString(R.string.yesterday);
            }
        }

        return DEFAULT_FORMAT.format(new Date(timestamp));

    }

}
