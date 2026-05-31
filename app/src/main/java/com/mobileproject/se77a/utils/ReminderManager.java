package com.mobileproject.se77a.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.receivers.MedicationReminderReceiver;

import java.util.Calendar;

public class ReminderManager {

    private final Context context;
    private final AlarmManager alarmManager;

    public ReminderManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Programme les alarmes pour un médicament
     * reminderTime peut contenir plusieurs horaires séparés par des virgules
     */
    public void scheduleMedicationAlarm(Medication med) {
        if (!med.isActive || med.reminderTime == null || med.reminderTime.isEmpty()) return;

        String[] times = med.reminderTime.split(",");
        String[] takenTimes = (med.takenTimes != null && !med.takenTimes.isEmpty()) ? med.takenTimes.split(",") : new String[0];

        for (int i = 0; i < times.length; i++) {
            String time = times[i].trim();
            if (time.isEmpty()) continue;

            // Check if this specific time was already taken today
            boolean alreadyTakenToday = false;
            for (String taken : takenTimes) {
                if (time.equals(taken.trim())) {
                    alreadyTakenToday = true;
                    break;
                }
            }

            String[] parts = time.split(":");
            if (parts.length != 2) continue;

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // Logic:
            // 1. If already taken today -> schedule for tomorrow.
            // 2. If not taken yet AND time has passed -> schedule for tomorrow.
            // 3. If not taken yet AND time is in future -> schedule for today.
            if (alreadyTakenToday || calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            // ID unique pour chaque alarme : ID_MEDICAMENT * 100 + INDEX_HORAIRE
            int alarmId = med.id * 100 + i;

            Intent intent = new Intent(context, MedicationReminderReceiver.class);
            intent.putExtra("med_id", med.id);
            intent.putExtra("med_name", med.name);
            intent.putExtra("med_dosage", med.dosage);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
            Log.d("ReminderManager", "Alarme programmée pour " + med.name + " à " + time);
        }
    }

    public void cancelMedicationAlarms(Medication med) {
        if (med.reminderTime == null) return;
        String[] times = med.reminderTime.split(",");
        for (int i = 0; i < times.length; i++) {
            int alarmId = med.id * 100 + i;
            Intent intent = new Intent(context, MedicationReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
        }
        Log.d("ReminderManager", "Alarmes annulées pour " + med.name);
    }
}
