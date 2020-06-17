package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.Random;

public class SuccessLogin extends AppCompatActivity {
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    Bundle x;
    Context context=this;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.x=savedInstanceState;
        setContentView(R.layout.activity_success_login);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Your Account is activated"); // set the top title

        String username=getIntent().getStringExtra("username");
        String latitude=getIntent().getStringExtra("latitude");
        String longitude=getIntent().getStringExtra("longitude");
          Random rd=new Random();
            rd.setSeed(1);
            int nextWait=rd.nextInt(4);
            int minute30=nextWait*1000;// 30*60*1000

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(SuccessLogin.this, RestartCOVIDService.class);
        alarmIntent.putExtra("username",username);
        alarmIntent.putExtra("latitude",latitude);
        alarmIntent.putExtra("longitude",longitude);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(SuccessLogin.this, 0, alarmIntent, 0);

        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+minute30, pendingIntent);
        Toast.makeText(this,"You can close this App now", Toast.LENGTH_LONG).show();

    }

}

