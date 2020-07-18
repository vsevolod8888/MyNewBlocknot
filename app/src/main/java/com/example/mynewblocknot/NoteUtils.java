package com.example.mynewblocknot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteUtils {
    public static String dateFromLong(long time){
        DateFormat format = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm", Locale.UK);
        return  format.format(new Date(time));

    }
}
