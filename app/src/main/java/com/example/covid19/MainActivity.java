package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TelephonyManager tel;
    EditText username, password;
    Button login, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        username = findViewById(R.id.editText2);
        password = findViewById(R.id.editText4);
        login = findViewById(R.id.button);
        signup = findViewById(R.id.button2);
        final String whetherSignup=getIntent().getStringExtra("click");

        final Intent mainToSignup = new Intent(MainActivity.this, Signup.class);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whetherSignup.equals("yes"))
                    startActivity(mainToSignup);
                else
                    Toast.makeText(MainActivity.this,"You cannot create more than one account from a single device",Toast.LENGTH_LONG).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().length()!=0 && password.getText().toString().length()!=0)
                {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("username", username.getText().toString());
                        json.put("password", password.getText().toString());
                        json.put("imeiNumber",tel.getImei());
                    }
                    catch (SecurityException e)
                    {

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    String url = "https://quarantinecovid19.herokuapp.com/login";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try
                                    {
                                        if(response.get("message").toString().equals("not"))
                                        {
                                            Toast.makeText(MainActivity.this,"Either Username or Password is wrong",Toast.LENGTH_LONG).show();

                                        }
                                        else
                                        {
                                            final Intent callToSuccessAct = new Intent(MainActivity.this, SuccessLogin.class);
                                            callToSuccessAct.putExtra("username",response.get("username").toString());
                                            callToSuccessAct.putExtra("latitude",response.get("lat").toString());
                                            callToSuccessAct.putExtra("longitude",response.get("longitude").toString());
                                            startActivity(callToSuccessAct);

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
                            Toast.makeText(MainActivity.this, "error occured in response", Toast.LENGTH_LONG).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                    Log.d("1", "response got for create button");
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Either username or password is empty",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}







