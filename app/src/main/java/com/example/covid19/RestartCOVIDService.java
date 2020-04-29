package com.example.covid19;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RestartCOVIDService extends BroadcastReceiver {
String username,latitude,longitude;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("2","Broadcast receiver");
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent serviceIntent = new Intent(context, SuccessLogin.class);
            context.startService(serviceIntent);
        } else
            {
                username=intent.getStringExtra("username");
                latitude=intent.getStringExtra("latitude");
                longitude=intent.getStringExtra("longitude");
                Intent callMiddleActivity=new Intent(context,middleActivity.class);
                callMiddleActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callMiddleActivity.putExtra("username",username);
                callMiddleActivity.putExtra("latitude",latitude);
                callMiddleActivity.putExtra("longitude",longitude);
                context.startActivity(callMiddleActivity);

        }
    }

}
