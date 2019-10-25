package com.example.interactly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class Home extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    //--MENU
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    //--

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

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
                startActivity(intent);
                break;
            case R.id.profile:
                Toast.makeText(this, "Start profile intent here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.qna:
                Toast.makeText(this, "Start qna intent here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.survey:
                Toast.makeText(this, "Start survey intent here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.polls:
                Toast.makeText(this, "Start poll intent here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                break;
            default:
                intent = new Intent(Home.this, Home.class);
                startActivity(intent);
                break;
        }
        return true;
    }
    //-------------------------------------------MENU CODE END

}
