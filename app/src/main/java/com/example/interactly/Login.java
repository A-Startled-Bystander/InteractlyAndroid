package com.example.interactly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    Button btnRegister, btnLogin, btnToSurvey;
    TextView txtLoginName;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Test if account info is correct


                // Creating GET url
                String sTest = "WhoaThere";
                String s = String.format("https://interactlyapi.azurewebsites.net/api/users/profile?param1=%1$s", sTest);
                Log.d("HIIIIIIIII", "onClick: " + s);
                // = https://interactlyapi.azurewebsites.net/api/users/profile?param1=WhoaThere
                // param1 must be name of field



                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
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
}
