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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.interactly.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Polls extends AppCompatActivity
{
    //Global variable
    String sToken, sJSON,user;
    ListView listP;
    List<String> titles = new ArrayList<String>();
    List<String> codes = new ArrayList<>();
    List<Integer> ids = new ArrayList<Integer>();
    PollAdapter adapter;
    Button btnNewPoll;
    private static final String TAG = "Polls";

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
        SendPollListRequest();

        listP.findViewById(R.id.pList);
//        listP.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//
//            }
//        });

        btnNewPoll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createPoll();
            }
        });


    }

    protected void SendPollListRequest()
    {
        titles.clear();
        codes.clear();
        ids.clear();
        String sUrl = "https://interactlyapi.azurewebsites.net/api/polls/polls?username="+user;
        try{
            StringRequest objectRequest = new StringRequest(Request.Method.GET, sUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    sJSON = response;
                    try{
                        // comprised of /me json response
                        JSONArray jsonArray = new JSONArray(sJSON);

                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject qna = jsonArray.getJSONObject(i);

                            String name = qna.getString("name");
                            String code = qna.getString("eventCode");
                            int id = qna.getInt("pollId");

                            titles.add(name);
                            codes.add(code);
                            ids.add(id);
                        }
                        adapter.notifyDataSetChanged();

                    }
                    catch (Exception e){
                        //Breakage
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error);
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
            Log.d(TAG, "SendPollListRequest: "+e.getMessage());
        }

    }


    protected void createPoll()
    {
       Intent intent = new Intent(this, CreatePolls.class);
       intent.putExtra("token",sToken);
       startActivity(intent);
    }


    private void SendUserDetailsReq(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/users/me";

        try{
            StringRequest objectRequest = new StringRequest(Request.Method.GET, sUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    sJSON = response;
                    try{
                        // comprised of /me json response
                        JSONObject objJSON = new JSONObject(sJSON);

                        user = objJSON.get("username").toString();
                        Log.d(TAG, "onResponse: "+user);

                       SendPollListRequest();
                    }
                    catch (Exception e){
                        //Breakage
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error);
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
            Log.d("Exception", e.getMessage());
        }
    }

    class PollAdapter extends ArrayAdapter<String>
   {
       Context context;
       List<String> myTitles;
       List<String>  myCodes;

       PollAdapter(Context c, List<String> titles, List<String> codes)
       {
           super(c,R.layout.poll_list,R.id.pTitle, titles);
           this.context=c;
           this.myTitles=titles;
           this.myCodes=codes;
       }

       @NonNull
       @Override
       public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
           LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           View plist = layoutInflater.inflate(R.layout.poll_list, parent, false);
           TextView myTitle = plist.findViewById(R.id.pTitle);
           TextView myDescription = plist.findViewById(R.id.pdescription);
           myTitle.setText(titles.get(position));
           myDescription.setText(codes.get(position));


           return plist;
       }
   }

}
