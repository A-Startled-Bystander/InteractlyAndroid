package com.example.interactly;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Polls extends AppCompatActivity
{
    //Global variable
    String sToken;
    ListView listQ;
    String titles[] = {"Test 1", "Test 2"};
    String descriptions[] = {"Description 1", "Description 2"};
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polls);

        //-- Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");
        Log.d("JWT token", "onCreate: " + sToken);

        listQ = findViewById(R.id.listQuestions);
        //get list content from database
        ///


        MyAdapter myAdapter = new MyAdapter(this, titles, descriptions);
        listQ.setAdapter(myAdapter);

        listQ.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String title = listQ.getItemAtPosition(position).toString();
                Toast.makeText(Polls.this,"This is the item selected"+title,Toast.LENGTH_SHORT).show();
            }
        });


    }

    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        String myTitles[];
        String myDescriptions[];

        MyAdapter(Context c, String[] titles, String[] descriptions)
        {
            super(c,R.layout.qrow,R.id.qtitle, titles);
            this.context=c;
            this.myTitles=titles;
            this.myDescriptions=descriptions;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View qrow = layoutInflater.inflate(R.layout.qrow, parent, false);
            TextView myTitle = qrow.findViewById(R.id.qtitle);
            TextView myDescription = qrow.findViewById(R.id.qdescription);
            myTitle.setText(titles[position]);
            myDescription.setText(descriptions[position]);

            return qrow;
        }
    }
}
