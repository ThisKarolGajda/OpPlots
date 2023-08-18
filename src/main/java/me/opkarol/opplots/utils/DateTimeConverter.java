package me.opkarol.opplots.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeConverter {
    public static String convertToReadableFormat(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyMMddHHmmss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", new Locale("pl", "PL"));

        try {
            Date date = inputFormat.parse(input);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }
}
