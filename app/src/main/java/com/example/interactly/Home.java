package com.example.interactly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class Home extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    //--MENU
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    //--
    String sToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        //-- Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");
        Log.d("HERE WE GOOOOOOOO", "onCreate: " + sToken);

        //---MENU
        mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(Home.this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(Home.this);
        navigationView.bringToFront();
        //---



    }


    //-------------------------------------------MENU CODE
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
        {return true;}
        return super.onOptionsItemSelected(item);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case R.id.home:
                intent = new Intent(Home.this, Home.class);
                intent.putExtra("token", sToken);
                startActivity(intent);
                break;
            case R.id.profile:
                intent = new Intent(Home.this, Profile.class);
                intent.putExtra("token", sToken);
                startActivity(intent);
                break;
            case R.id.qna:
                intent = new Intent(Home.this, Qna.class);
                intent.putExtra("token", sToken);
                startActivity(intent);
                break;
            case R.id.survey:
                Toast.makeText(getApplicationContext(), "Feature coming soon", Toast.LENGTH_SHORT);
            case R.id.polls:
                intent = new Intent(Home.this, Polls.class);
                intent.putExtra("token",sToken);
                startActivity(intent);
                break;
            case R.id.logout:
                intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                break;

        }
        return true;
    }
    //-------------------------------------------MENU CODE END

}
