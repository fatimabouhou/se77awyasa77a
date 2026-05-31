package com.mobileproject.se77a.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    /**
     * Convertit une chaîne "HH:mm" (24h) en format local (12h AM/PM ou 24h)
     */
    public static String formatTimeForDisplay(Context context, String hhmm) {
        if (hhmm == null || hhmm.isEmpty() || hhmm.equals("--:--") || hhmm.equals("Terminé")) {
            return hhmm;
        }
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf24.parse(hhmm);
            if (date == null) return hhmm;
            
            return DateFormat.getTimeFormat(context).format(date);
        } catch (Exception e) {
            return hhmm;
        }
    }
}
