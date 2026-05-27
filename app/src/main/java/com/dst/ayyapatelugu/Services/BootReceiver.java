package com.dst.ayyapatelugu.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Intent i = new Intent(context,
                    LocationForegroundService.class);

            ContextCompat.startForegroundService(context, i);
        }
    }
}
