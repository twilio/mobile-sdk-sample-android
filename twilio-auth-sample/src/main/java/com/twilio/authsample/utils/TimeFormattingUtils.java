package com.twilio.authsample.utils;

import android.content.Context;

import com.twilio.authsample.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jsuarez on 3/14/16.
 */
public class TimeFormattingUtils {
    public static final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm aa", Locale.US);

    public static String formatExpirationTime(long expirationTimestamp) {
        // TODO: Include moving factor
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(expirationTimestamp*1000);
        Date date = cal.getTime();
        return TimeFormattingUtils.EXPIRATION_DATE_FORMAT.format(date);
    }

    public static String formatTransactionTime(Context context, Date creationDate) {
        long nowTime = new Date().getTime();
        long agoTime = creationDate.getTime();

        long diffInMillis = nowTime - agoTime;
        long secs = Math.abs(diffInMillis) / 1000;
        long mins = secs / 60;
        long hours = mins / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = months / 12;

        if (years > 0) {
            return context.getString(R.string.time_year, years);
        } else if (months > 0) {
            return context.getString(R.string.time_month, months);
        } else if (days > 0) {
            return context.getString(R.string.time_day, days);
        } else if (hours > 0) {
            return context.getString(R.string.time_hour, hours);
        } else if (mins > 0) {
            return context.getString(R.string.time_min, mins);
        } else {
            return context.getString(R.string.time_sec, secs);
        }
    }
}
