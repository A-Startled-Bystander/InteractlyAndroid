package com.example.interactly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    String sToken;
    ImageButton btnEdit;
    Button btnSave, btnPasswords;
    EditText txtPPassword, txtPPasswordConfirm, txtPEmail;
    TextView txtPUsername;
    String sJSON = "", sUsername = "", sEmail = "", sPassword = "", sConfirmPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");

        Init();
    }

    private void Init(){
        btnEdit = findViewById(R.id.imgbtnPEdit);
        btnSave = findViewById(R.id.btnPSave);
        btnPasswords = findViewById(R.id.btnPChangePassword);
        txtPPassword = findViewById(R.id.txtPPassword);
        txtPPasswordConfirm = findViewById(R.id.txtPPasswordConfirm);
        txtPEmail = findViewById(R.id.txtPEmail);
        txtPUsername = findViewById(R.id.txtPUserName);

        txtPPassword.setText("");
        txtPPassword.setVisibility(View.INVISIBLE);
        txtPPassword.setEnabled(false);
        txtPPasswordConfirm.setText("");
        txtPPasswordConfirm.setVisibility(View.INVISIBLE);
        txtPPasswordConfirm.setEnabled(false);

        txtPEmail.setEnabled(false);
        btnPasswords.setEnabled(false);

        Log.d("HELOO THERE FRIEND", "INIT: " + sToken);
        SendUserDetailsReq();
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPEmail.setEnabled(true);
                btnPasswords.setEnabled(true);
            }
        });

        btnPasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPasswords.setVisibility(View.INVISIBLE);
                btnPasswords.setEnabled(false);
                txtPPassword.setVisibility(View.VISIBLE);
                txtPPassword.setEnabled(true);
                txtPPasswordConfirm.setVisibility(View.VISIBLE);
                txtPPasswordConfirm.setEnabled(true);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = txtPEmail.getText().toString();
                sPassword = txtPPassword.getText().toString();
                sConfirmPass = txtPPasswordConfirm.getText().toString();

                Boolean bValue = ValidateData();
                if (bValue){
                    Save();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid email/password", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    private void SendUserDetailsReq(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/users/me";

        try{
            StringRequest objectRequest = new StringRequest(Request.Method.GET, sUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    sJSON = response.toString();
                    try{
                        // comprised of /me json response
                        JSONObject objJSON = new JSONObject(sJSON);

                        sUsername = objJSON.get("username").toString();
                        sEmail = objJSON.get("email").toString();
                        txtPUsername.setText(sUsername);
                        txtPEmail.setText(sEmail);
                    }
                    catch (Exception e){
                        //Breakage
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("HELLOOOOOOOOOOO", "OOPS ");
                }
            })
            {
                // Add JWT Token to the request header
                @Override
                public Map getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer "+ sToken);
                    return params;
                }

            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch(Exception e){
            Log.d("HELLOOOOOOOOOOO", "EISH ");
        }

    }

    private void DisplayInfo(){



    }

    private boolean ValidateData(){
        Boolean bValue = true;

        // Check passwords if texts are enabled
        if (txtPPassword.isEnabled()){
            if ((sConfirmPass.length() < 8) || (sPassword.length() < 8)){
                Log.d("HERE", "ValidateData: I GO FALSE HERE");
                bValue = false;
            }

            if (!sPassword.equals(sConfirmPass)){
                bValue = false;
            }
        }

        if ((!sEmail.contains("@")) || (!sEmail.contains(".com"))){
            Log.d("HERE", "ValidateData: I GO FALSE HERE2");
            bValue = false;
        }

        return bValue;
    }



    private void Save(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/users/update";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", sEmail);
            jsonBody.put("password", sPassword);
            jsonBody.put("passwordConfirmation", sConfirmPass);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PATCH, sUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT);
                    toast.show();

                    finish();
                    startActivity(getIntent());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MY UPDATE RESPONSE", "onResponse: THE BISH DOESNT WORK" );
                }
            })
            {
                // Add JWT Token to the request header
                @Override
                public Map getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer "+ sToken);
                    return params;
                }

            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch (Exception e){
            // Broke
        }
    }

}
