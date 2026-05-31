package com.mobileproject.se77a.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobileproject.se77a.utils.NotificationHelper;

public class MedicationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d("MedicationReceiver", "Rappel reçu !");
        int medId = intent.getIntExtra("med_id", -1);
        String medName = intent.getStringExtra("med_name");
        String dosage = intent.getStringExtra("med_dosage");

        if (medId != -1 && medName != null) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showMedicationNotification(medId, medName, dosage);
        } else {
            android.util.Log.e("MedicationReceiver", "Données manquantes dans l'intent");
        }
    }
}
