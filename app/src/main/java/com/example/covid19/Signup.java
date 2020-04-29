package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class Signup extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView DisplayMsg;
    EditText username,password;
    Button create,back,fetchDetails;
    ImageView clickImage;
    String IMEI;
    LocationManager locationManager;
    Context c=this;
    String state=null,city=null,pincode=null,locality=null,district=null;
    String longitude=null,latitude=null;
    int gotLocation=1,gotImage=0,gotIMEI=0,gotDetails=0;
    Bitmap imageBitmap;
    EditText fullname;
    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.d("1","location change");
            latitude=Double.toString(location.getLatitude());
            longitude=Double.toString(location.getLongitude());
                 Log.d("8",longitude+" long "+latitude+" lat");
                 DisplayMsg.setText("Got Your Current Location");
                 gotLocation=1;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("1","status changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("1","provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("1","provider disabled");

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        fetchDetails=findViewById(R.id.button7);
        username=findViewById(R.id.editText);
        password=findViewById(R.id.editText3);
        create=findViewById(R.id.button3);
        back=findViewById(R.id.button5);
        DisplayMsg=findViewById(R.id.textView);
        DisplayMsg.setText("Wait while we are searching your location !");
        clickImage=findViewById(R.id.clickImage);
        fullname=findViewById(R.id.editText5);
        TelephonyManager tel;

        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {

            IMEI = tel.getImei();
            gotIMEI = 1;
        }
        catch(SecurityException e)
        {

        }
        locationManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);

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

    fetchDetails.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fetchDetails.setEnabled(false);
            RequestQueue rq=Volley.newRequestQueue(Signup.this);
            String url2 = "https://apis.mapmyindia.com/advancedmaps/v1/godle5rpj4rpt7ikq4jtaha378bvlw4d/rev_geocode?lat="+latitude+"&lng="+longitude;
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url2, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                             try {
                                    JSONArray j=response.getJSONArray("results");
                                    JSONObject ji=j.getJSONObject(0);
                                    city=ji.get("city").toString();
                                    pincode=ji.get("pincode").toString();
                                    locality=ji.get("locality").toString();
                                    district=ji.get("district").toString();
                                    gotDetails=1;
                                 fetchDetails.setEnabled(false);
                             } catch (Exception e) {
                                 fetchDetails.setEnabled(true);
                                Log.d("2","error "+e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    }



            });
            rq.add(postRequest);
        }
    });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "create button pressed");

                create.setEnabled(false);
                if (gotImage == 1 && gotLocation == 1 && gotIMEI==1 && gotDetails==1)
                {
                    if (username.getText().toString().length() != 0 && password.getText().toString().length() != 0)
                    {
                        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                        File file = wrapper.getDir("Images",MODE_PRIVATE);
                        file = new File(file, "Image"+".jpg");
                        try {
                            OutputStream stream = null;
                            stream = new FileOutputStream(file);
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG,20,stream);
                            stream.flush();
                            stream.close();
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JSONObject json = new JSONObject();
                        try {
                            json.put("name",fullname.getText().toString());
                            json.put("username",username.getText().toString());
                            json.put("password",password.getText().toString());
                            json.put("lat",latitude);
                            json.put("longitude",longitude);
                            json.put("state",state);
                            json.put("city",city);
                            json.put("pincode",pincode);
                            json.put("locality",locality);
                            json.put("district",district);
                            json.put("imeiNumber",IMEI);
                        }
                        catch(JSONException e) {
                            Log.d("5","exception caught");
                        }

                       RequestQueue requestQueue = Volley.newRequestQueue(Signup.this);
                        String url = "https://quarantinecovid19.herokuapp.com/signup";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try
                                        {

                                            if( response.get("message").toString().equals("created"))
                                            {
                                                create.setEnabled(true);
                                                Toast.makeText(Signup.this,"Your account is successfully created",Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                create.setEnabled(true);
                                                Toast.makeText(Signup.this,"Sorry, This username is already in use",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                        catch (JSONException e)
                                        {

                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                create.setEnabled(true);
                                Toast.makeText(Signup.this,"error occured during response",Toast.LENGTH_LONG).show();                    }
                        });
                            Log.d("1","response got for create button");
                            requestQueue.add(jsonObjectRequest);

                } else {
                        create.setEnabled(true);
                        Toast.makeText(Signup.this, "Either your username or password is empty.", Toast.LENGTH_LONG).show();
                    }
                } else
                    {
                    if (gotLocation == 0) {
                        create.setEnabled(true);
                        Toast.makeText(Signup.this, "Wait while we are accessing your location.", Toast.LENGTH_LONG).show();
                    }else
                        {
                            if(gotDetails==0) {
                                create.setEnabled(true);
                                Toast.makeText(Signup.this, "Wait while we are accessing your details", Toast.LENGTH_LONG).show();
                            }
                            else {
                                create.setEnabled(true);
                                Toast.makeText(Signup.this, "!! You have not clicked your picture.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupToLogin=new Intent(Signup.this,MainActivity.class);
                startActivity(signupToLogin);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            gotImage=1;

        }
    }
}
