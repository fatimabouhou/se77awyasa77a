package com.mobileproject.se77a.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mobileproject.se77a.repository.MedicationRepository;
import com.mobileproject.se77a.utils.NotificationHelper;

public class MedicationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int medId = intent.getIntExtra("med_id", -1);
        String medName = intent.getStringExtra("med_name");

        if ("ACTION_MARK_TAKEN".equals(action) && medId != -1) {
            // 1. Annuler la notification immédiatement pour arrêter le son
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.cancelNotification(medId);

            // 2. Marquer comme pris dans la base de données
            MedicationRepository repository = new MedicationRepository((android.app.Application) context.getApplicationContext());
            repository.markNextDoseAsTaken(medId);

            Toast.makeText(context, medName + " marqué comme pris ✓", Toast.LENGTH_SHORT).show();
        }
    }
}
