package com.example.covid19;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class COVID extends Service {

    String username, imagePath, latitude, longitude;

    public COVID() {
    }

    public int onStartCommand(Intent intent, int status, int id) {
        super.onStartCommand(intent, status, id);
        Log.d("5","COVID service onstart Command");
        username = intent.getStringExtra("username");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        try {
            Random rd=new Random();
            rd.setSeed(2);
            int nextWait=rd.nextInt(5);
            final Handler handler = new Handler();
            int minute30=30*60*1000;
            handler.postDelayed(new Runnable() {
                @Override

                public void run() {

                    Log.d("2", "call to middle Activity");
                    Intent callToMiddleAct = new Intent(COVID.this, middleActivity.class);
                    callToMiddleAct.putExtra("username", username);
                    callToMiddleAct.putExtra("latitude", latitude);
                    callToMiddleAct.putExtra("longitude", longitude);
                    callToMiddleAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callToMiddleAct);
                }

            },  minute30*nextWait);
        }
        catch (RuntimeException e)
        {
            Log.d("1","Run time exception caught in middle Activity");

        }
        catch (Exception e)
        {
            Log.d("1","exception caught in middle Activity");
        }

        return COVID.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.d("5","COVID service destroyed");
        stopSelf();
    }
    @Override
    public void onCreate() {
        Log.d("1", "COVID service created");



    }
}

