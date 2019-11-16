package com.example.interactly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Register extends AppCompatActivity {
    private String sName,sEmail,sPass,sConfirmPass;
    TextView txtName,txtEmail,txtPass,txtConfirmPass;
    Button btnSubmit, btnRCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Init();
    }

    private void Init(){
        btnSubmit = findViewById(R.id.btnRSubmit);
        btnRCancel = findViewById(R.id.btnRCancel);
        txtName = findViewById(R.id.txtRUsername);
        txtEmail = findViewById(R.id.txtREmail);
        txtPass = findViewById(R.id.txtRPassword);
        txtConfirmPass = findViewById(R.id.txtRConfirmPass);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sName = txtName.getText().toString();
                sEmail = txtEmail.getText().toString();
                sPass = txtPass.getText().toString();
                sConfirmPass = txtConfirmPass.getText().toString();

                if(sName!="") {
                    if(sEmail!=""){
                        if(sPass!="") {
                            if (sPass.equals(sConfirmPass)) {
                                SendReq();
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);

                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Password must match", Toast.LENGTH_SHORT);
                                toast.show();
                                txtConfirmPass.setText("");
                                txtPass.setText("");
                            }
                        }else{Toast.makeText(Register.this, "Please enter a Password", Toast.LENGTH_SHORT).show();}
                    }else{Toast.makeText(Register.this, "Please enter a Email", Toast.LENGTH_SHORT).show();}
                }else{Toast.makeText(Register.this, "Please enter a Username", Toast.LENGTH_SHORT).show();}
            }
        });

        btnRCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void SendReq(){
        String sURL = "https://interactlyapi.azurewebsites.net/api/users/register";

        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username",sName);
            jsonBody.put("password", sPass);
            jsonBody.put("email", sEmail);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, sURL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast toast = Toast.makeText(getApplicationContext(),"You have successfully created an account", Toast.LENGTH_SHORT);
                    toast.show();



                    // Retrieving JSON response
                    String s = response.toString();
                    try{

                        JSONObject obj = new JSONObject(s);
                        Log.d("WE CONVERTED IT: ", "Username and password: " + obj.get("username"));
                    }
                    catch(Exception ex){
                        Log.d("MyFailTag", "COULDNT CONVERT THE HOE " + ex);
                    }




                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error creating account", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch (Exception e){
            Log.d("Uhm Yeaaaaaa", "Somethings not right: ");
        }

    }
}
