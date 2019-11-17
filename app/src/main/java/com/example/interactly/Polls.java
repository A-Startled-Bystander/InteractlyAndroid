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
import android.widget.Button;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


public class Polls extends AppCompatActivity
{
    //Global variable
    String sToken, sJSON,user;
    ListView listPolls;
    List<String> titles = new ArrayList<String>();
    List<String> codes  = new ArrayList<String>();
    List<Integer> ids = new ArrayList<Integer>();
    MyAdapter myAdapter;
    Button btnNewPoll;



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

        SendUserDetailsReq();

        listPolls = findViewById(R.id.listPolls);
        btnNewPoll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               // createPoll();
            }
        });


        MyAdapter myAdapter = new MyAdapter(this, titles, codes);
        listPolls.setAdapter(myAdapter);

        listPolls.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String title = listPolls.getItemAtPosition(position).toString();
                Toast.makeText(Polls.this,"This is the item selected"+title,Toast.LENGTH_SHORT).show();
            }
        });


    }


    protected void getUsersPolls()
    {
        titles.clear();
        codes.clear();
        ids.clear();

        String sUrl = "https://interactlyapi.azurewebsites.net/api/polls/polls?username="+user;

        try{
            StringRequest objectRequest = new StringRequest(Request.Method.GET, sUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    sJSON = response.toString();
                    try{
                        // comprised of /me json response
                        JSONArray jsonArray = new JSONArray(sJSON);

                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject poll = jsonArray.getJSONObject(i);

                            String name = poll.getString("name");
                            String code = poll.getString("eventCode");
                            int id = poll.getInt("pollId");

                            titles.add(name);
                            codes.add(code);
                            ids.add(id);
                        }
                        myAdapter.notifyDataSetChanged();

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
    protected void createPoll()
    {
       Intent intent = new Intent(this,CreatePolls.class);
       intent.putExtra("token",sToken);
       startActivity(intent);
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

                        user = objJSON.get("username").toString();

                        //populate listview
                       getUsersPolls();
                    }
                    catch (Exception e){
                        //Breakage
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error User Retrieval", "Something went wrong");
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

    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        List<String> myTitles;
        List<String>  myCodes;

        MyAdapter(Context c, List<String> titles, List<String> codes)
        {
            super(c,R.layout.qrow,R.id.qtitle, titles);
            this.context=c;
            this.myTitles=titles;
            this.myCodes=codes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View qrow = layoutInflater.inflate(R.layout.qrow, parent, false);
            TextView myTitle = qrow.findViewById(R.id.qtitle);
            TextView myDescription = qrow.findViewById(R.id.qdescription);
            myTitle.setText(titles.get(position));
            myDescription.setText(codes.get(position));

            return qrow;
        }
    }
}
