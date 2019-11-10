package com.example.interactly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Login extends AppCompatActivity {
    Button btnRegister, btnLogin, btnToSurvey;
    TextView txtLName,txtLPass;
    String sName,sPass,sEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Init();

    }

    public void Init(){
        //Initiate Objects
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnToSurvey = findViewById(R.id.btnLGo);
        txtLName = findViewById(R.id.txtLName);
        txtLPass = findViewById(R.id.txtLPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Test if account info is correct


                // Creating GET url
                //String sTest = "WhoaThere";
                //String s = String.format("https://interactlyapi.azurewebsites.net/api/users/profile?param1=%1$s", sTest);
                //Log.d("HIIIIIIIII", "onClick: " + s);
                // = https://interactlyapi.azurewebsites.net/api/users/profile?param1=WhoaThere
                // param1 must be name of field


                sName = txtLName.getText().toString();
                sPass = txtLPass.getText().toString();
                sEmail = "";
                SendReq();

                //Intent intent = new Intent(Login.this, Home.class);
                //startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send to register page
                Intent myIntent = new Intent(v.getContext(),Register.class);
                startActivity(myIntent);
            }
        });

        btnToSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(Login.this, "Use code and go to survey... TODO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendReq(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/users/authenticate";

        Log.d("MADE IT HERE", "HELLO: ");


        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username",sName);
            jsonBody.put("password", sPass);
            jsonBody.put("email", sEmail);

            Log.d("MADE IT HERE", "HELLO2: ");


            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, sUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    // Retrieving JSON response
                    String sToken = response.toString();
                    try{

                        JSONObject objToken = new JSONObject(sToken);
                        sToken = objToken.get("token").toString();
                    }
                    catch(Exception ex){
                        Log.d("MyFailTag", "COULDNT CONVERT THE HOE " + ex);
                    }

                    Intent intent = new Intent(Login.this, Home.class);
                    intent.putExtra("token", sToken);
                    startActivity(intent);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast toast = Toast.makeText(getApplicationContext(), "ERROR: Could Not Log In", Toast.LENGTH_SHORT);
                    toast.show();

                }
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch(Exception e){
            Log.d("FUCKUP VEN MORE", e.toString());
        }
    }
}
