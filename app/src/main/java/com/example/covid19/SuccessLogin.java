package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SuccessLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_login);

        String username=getIntent().getStringExtra("username");
        String latitude=getIntent().getStringExtra("latitude");
        String longitude=getIntent().getStringExtra("longitude");
        String imagePath=getIntent().getStringExtra("imagePath");


        Intent monitoringService= new Intent(SuccessLogin.this,COVID.class);
        monitoringService.putExtra("username",username);
        monitoringService.putExtra("latitude",latitude);
        monitoringService.putExtra("longitude",longitude);
        monitoringService.putExtra("imagePath",imagePath);
        Log.d("2","successLogin Activity");
        startService(monitoringService);
    }
}
