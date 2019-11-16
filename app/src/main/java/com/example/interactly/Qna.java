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

public class Qna extends AppCompatActivity implements HostQna.HostQnaListener
{

    String sToken, user;
    ListView listQ;
    List<String> titles = new ArrayList<String>();
    List<String> codes  = new ArrayList<String>();
    MyAdapter adapter;


    String sJSON, sQnaTitle;

    Button btnNewQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qna);

        //-- Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");
        user = intent.getStringExtra("user");
        Log.d("HERE WE GOOOOOOOO", "onCreate: " + sToken);

        //get questions from database
        SendQuestionListReq();

        btnNewQuestion = findViewById(R.id.btnNewQuestion);
        listQ = findViewById(R.id.listQuestions);

        //get list content from database
        ///

        adapter = new MyAdapter(this, titles, codes);
        listQ.setAdapter(adapter);

        //when clicked, go to answer question
        listQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String title = listQ.getItemAtPosition(position).toString();
               Toast.makeText(Qna.this, "This it item "+ title, Toast.LENGTH_SHORT).show();
           }
       });

        btnNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
              //Opens pop up to get new Qna title
            }
        });

    }

    public void openDialog()
    {
        HostQna hostQna = new HostQna();
        hostQna.show(getSupportFragmentManager(), "Host New QnA");
    }

    @Override
    public void getQnaTitle(String qnaTitle) {
        //get Name and post qna question
        if(qnaTitle.length()<8||qnaTitle.length()>30)
        {
            Toast.makeText(this, "Please enter in a title between 8-30 characters.", Toast.LENGTH_SHORT).show();
        }
        else
            {
                sQnaTitle = qnaTitle;
                //reset lists to re add with new QnA


                SendQnaCreateReq();
                SendQuestionListReq();

        }



    }

    private void SendQnaCreateReq(){
        titles.clear();
        codes.clear();

        String sURL = "https://interactlyapi.azurewebsites.net/api/qna/create";

        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name",sQnaTitle);


            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, sURL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast toast = Toast.makeText(getApplicationContext(),"You have successfully hosted a QnA", Toast.LENGTH_SHORT);
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
            Log.d("Uhm Yeaaaaaa", "Somethings not right: ");
        }

    }

    public void SendQuestionListReq(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/qna/sessions?username="+user;

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
                            JSONObject qna = jsonArray.getJSONObject(i);

                            String name = qna.getString("name");
                            String code = qna.getString("eventCode");

                            titles.add(name);
                            codes.add(code);
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
