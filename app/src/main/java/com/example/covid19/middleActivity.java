package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.AlarmManager.*;
import static com.example.covid19.Signup.REQUEST_IMAGE_CAPTURE;
import static java.lang.StrictMath.abs;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.media.MediaPlayer;
public class middleActivity extends AppCompatActivity {

    String username;
    double latitude = 0, longitude = 0, NewLongitude = 0, NewLatitude = 0;
    String quarantineAns = null;
    LocationManager locationManager;
    Button sendButton;
    ImageView clickImage;
    TextView msg;
    private MediaPlayer mediaPlayer;
    int sameFace = 0, samePlace = 0, checkedFace = 0,checkedLocation=0;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.d("1", "location change in middle Activity");

            NewLongitude = Double.valueOf(location.getLongitude());
            NewLatitude = Double.valueOf(location.getLatitude());
            msg.setText("Got your Location");
            checkedLocation = 1;
            if (NewLongitude != 0 && NewLatitude != 0) {
                if (abs(NewLatitude - latitude) <= 0.005 && abs(NewLongitude - longitude) <= 0.005) {
                    samePlace = 1;
                } else {
                    samePlace = 0;
                }
                Log.d("2", "lat= " + NewLatitude + ", long= " + NewLongitude);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("1", "status changed in middleActivity");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("1", "provider enabled in middleActivity");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("1", "provider disabled in middleActivity");

        }
    };
    void checkStatus() {

        Log.d("1", "samePlace="+samePlace);
        if (samePlace == 1 && sameFace == 1)
            quarantineAns = "yes";
        else
            quarantineAns = "no";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("status", "active");
            json.put("quarantine", quarantineAns);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(middleActivity.this);
        String url = "https://quarantinecovid19.herokuapp.com/update";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(middleActivity.this, "response=" + response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            if (response.get("message").toString().equals("added")) {
                                Toast.makeText(middleActivity.this, "Your Updates Sent Successfully", Toast.LENGTH_LONG).show();
                            }
                            else
                                {
                                Toast.makeText(middleActivity.this, "Either Username or Password is wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e)
                        {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(middleActivity.this, "error occured in response", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
        Log.d("1", "response got for middleActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle);

        msg = findViewById(R.id.textView6);
        msg.setText("Wait, while we are accessing your location !");
        clickImage = findViewById(R.id.button4);
        sendButton = findViewById(R.id.button6);


        Log.d("1", "middleActivity created");

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.start();

        username = getIntent().getStringExtra("username");
        latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
        longitude = Double.valueOf(getIntent().getStringExtra("longitude"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                sendButton.setEnabled(false);
                if (checkedLocation==1 && checkedFace == 1)
                {
                    checkStatus();

                    Random rd=new Random();
                    rd.setSeed(1);
                    int nextWait=rd.nextInt(4);
                    int minute30=nextWait*1000;// 30*60*1000
                    sendButton.setEnabled(true);
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent(middleActivity.this, RestartCOVIDService.class);
                    alarmIntent.putExtra("username",username);
                    alarmIntent.putExtra("latitude",Double.toString(latitude));
                    alarmIntent.putExtra("longitude",Double.toString(longitude));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(middleActivity.this, 0, alarmIntent, 0);
                    manager.setExact(RTC_WAKEUP, System.currentTimeMillis()+minute30, pendingIntent);
                } else {
                    if (checkedFace == 0) {
                        sendButton.setEnabled(true);
                        Toast.makeText(middleActivity.this, "Wait your face is being recognized", Toast.LENGTH_LONG).show();
                    }else {
                        sendButton.setEnabled(true);
                        Toast.makeText(middleActivity.this, "Wait While your Location is beign Accessed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        clickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImage.setEnabled(false);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationListener=null;
    }

    protected void onResume()
    {
        super.onResume();
        Log.d("1","Resume");
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        catch(SecurityException e)
        {

        }
    }
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data"); // new image clicked

            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            File directory = wrapper.getDir("Images", Context.MODE_PRIVATE);
            File file = new File(directory, "Image" + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath()); // previous image stored

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            byte[] byteArray1 = stream.toByteArray();

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream1);
            byte[] byteArray2 = stream1.toByteArray();

               final String encoded1 = Base64.encodeToString(byteArray1, Base64.DEFAULT);
               final String encoded2 = Base64.encodeToString(byteArray2, Base64.DEFAULT);

               Log.d("4","before face++ Api");
            String REGISTER_URL = "https://api-us.faceplusplus.com/facepp/v3/compare";
            RequestQueue queue = Volley.newRequestQueue(middleActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        JSONObject obj;
                        @Override
                        public void onResponse(String response) {
                             try
                             {
                                obj = new JSONObject(response);
                             }
                             catch (JSONException e)
                             {

                             }
                                try
                                {
                                    if (Double.valueOf(obj.get("confidence").toString()) > 80)
                                     {
                                        sameFace = 1;
                                        checkedFace = 1;
                                    } else {
                                        sameFace = 0;
                                        checkedFace = 1;
                                    }
                                    Log.d("4","sameFace="+sameFace);
                                }
                                catch (JSONException e) {
                                    sameFace = 0;
                                    checkedFace = 1;
                                }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override

                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(middleActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("api_key", "uO5S2E0kEfiLPl1oQFVrPLECEEhD1m01");
                    params.put("api_secret", "kBGAV4RVmPElmxZa3nDiuHbzJ_cCOx77");
                    params.put("image_base64_1", encoded1);
                    params.put("image_base64_2", encoded2);
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","multipart/form-data");
                    return params;
                }
            };
            queue.add(stringRequest);

        }
    }

}