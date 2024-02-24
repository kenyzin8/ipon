package com.kentj.ipon;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {
    public static String intcomma(String n) {
        String number = n;
        double amount = Double.parseDouble(number);

        if (amount == 0.0) return "0.00";

        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(amount);
    }

    public static String convertDate(String d){
        String originalDate = d;
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        Date date = null;
        try {
            date = inputFormat.parse(originalDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputFormat.format(date);
    }


}
