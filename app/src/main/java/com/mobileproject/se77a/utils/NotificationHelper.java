package com.mobileproject.se77a.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.activities.MainActivity;

public class NotificationHelper extends ContextWrapper {

    // On change l'ID du canal pour forcer la mise à jour des paramètres (son, importance)
    public static final String CHANNEL_MEDICATIONS_ID = "medications_alarms_v2";
    public static final String CHANNEL_MEDICATIONS_NAME = "Alarmes de médicaments";

    public static final String CHANNEL_APPOINTMENTS_ID = "appointments_reminders";
    public static final String CHANNEL_APPOINTMENTS_NAME = "Rappels de rendez-vous";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Son d'alarme système
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            // Channel for Medications (URGENT / ALARME)
            NotificationChannel medChannel = new NotificationChannel(
                    CHANNEL_MEDICATIONS_ID,
                    CHANNEL_MEDICATIONS_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            medChannel.setDescription("Alarmes sonores pour la prise de médicaments");
            
            // Configurer le son au niveau du canal (obligatoire pour Android 8+)
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            medChannel.setSound(alarmSound, audioAttributes);
            
            medChannel.enableLights(true);
            medChannel.enableVibration(true);
            medChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Channel for Appointments
            NotificationChannel apptChannel = new NotificationChannel(
                    CHANNEL_APPOINTMENTS_ID,
                    CHANNEL_APPOINTMENTS_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager nm = getManager();
            if (nm != null) {
                nm.createNotificationChannel(medChannel);
                nm.createNotificationChannel(apptChannel);
            }
        }
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public void showMedicationNotification(int medId, String medName, String dosage) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, medId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_MEDICATIONS_ID)
                .setSmallIcon(R.drawable.ic_medication)
                .setContentTitle("🚨 ALERTE MÉDICAMENT")
                .setContentText("C'est l'heure de votre traitement : " + medName + " (" + dosage + ")")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(alarmSound)
                // Rend la notification persistante et plein écran si possible
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        
        // FLAG_INSISTENT fait sonner la notification en boucle jusqu'à interaction
        notification.flags |= Notification.FLAG_INSISTENT;

        getManager().notify(medId, notification);
    }

    public void showAppointmentNotification(int apptId, String doctorName, String time) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_APPOINTMENTS_ID)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle("📅 Rappel Rendez-vous")
                .setContentText("Rendez-vous avec " + doctorName + " à " + time)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        getManager().notify(apptId + 1000, builder.build());
    }
}
