package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import java.util.Random;
import static android.app.AlarmManager.RTC_WAKEUP;

public class SuccessLogin extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_login);

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

        manager.setExact(RTC_WAKEUP, System.currentTimeMillis()+minute30, pendingIntent);
        Toast.makeText(this,"You can close this App now",Toast.LENGTH_LONG).show();
    }


}

