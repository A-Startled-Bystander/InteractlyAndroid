package com.example.interactly;

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

public class Register extends AppCompatActivity {
    private String sName,sEmail,sPass,sConfirmPass;
    TextView txtName,txtEmail,txtPass,txtConfirmPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Init();
    }

    private void Init(){
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtName = (TextView) findViewById(R.id.txtUsername);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPass = (TextView) findViewById(R.id.txtPassword);
        txtConfirmPass = (TextView) findViewById(R.id.txtConfirmPass);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sName = txtName.getText().toString();
                sEmail = txtEmail.getText().toString();
                sPass = txtPass.getText().toString();
                sConfirmPass = txtConfirmPass.getText().toString();

                if (sPass.equals(sConfirmPass)){
                    SendReq();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Password must match", Toast.LENGTH_SHORT);
                    toast.show();
                    txtConfirmPass.setText("");
                    txtPass.setText("");
                }
            }
        });
    }

    private void SendReq(){
        String sURL = "";
        Log.d("Uhm Yeaaaaaa", "Made it ");

        try{
            Log.d("Uhm Yeaaaaaa", "Attempting ");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username",sName);
            jsonBody.put("password", sPass);
            jsonBody.put("email", sEmail);

            Log.d("Uhm Yeaaaaaa", "Alriught ");

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, sURL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(),"No Success", Toast.LENGTH_SHORT);
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
