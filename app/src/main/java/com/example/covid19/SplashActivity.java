package com.example.covid19;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    ProgressDialog progressBar;
    int stopLoading = 0;

    TelephonyManager tel;
    String IMEInumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        progressBar = new ProgressDialog(SplashActivity.this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Loading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        new Thread(new Runnable() {
            public void run() {
                while (stopLoading==0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.dismiss();
            }
        }).start();

        JSONObject json = new JSONObject();
        try {

            IMEInumber = tel.getImei();
        }
        catch (SecurityException e)
        {

        }
        try {
        json.put("imeiNumber", IMEInumber);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        String url = "https://quarantinecovid19.herokuapp.com/checkDevice";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            if(response.get("message").toString().equals("not"))
                            {
                                Intent i=new Intent(SplashActivity.this,MainActivity.class);
                                i.putExtra("click","yes");
                                stopLoading=1;
                                startActivity(i);
                            }
                            else
                            {
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("imeiNumber",IMEInumber);
                                }
                                catch(JSONException e)
                                {

                                }
                                RequestQueue requestQueue1 = Volley.newRequestQueue(SplashActivity.this);

                                String url1="https://quarantinecovid19.herokuapp.com/LoginCheck";
                                JsonObjectRequest jsonObjectRequest1=new JsonObjectRequest(Request.Method.POST,url1,js,
                                        new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try
                                        {
                                            if(response.get("message").toString().equals("not"))
                                            {
                                                Intent i=new Intent(SplashActivity.this,MainActivity.class);
                                                i.putExtra("click","no");
                                               stopLoading=1;
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                stopLoading=1;
                                                Toast.makeText(SplashActivity.this,"Sorry, Your are already login, According to our policy you can only login once.",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                        catch(Exception e)
                                        {
                                            Log.d("3","exception caught in login");
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(SplashActivity.this, "error occured in response", Toast.LENGTH_LONG).show();
                                    }
                                });
                                requestQueue1.add(jsonObjectRequest1);
                            }

                        }
                        catch(Exception e)
                        {
                            Log.d("3","exception caught in login");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, "error occured in response", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);



    }
}
