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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class QnaAsk extends AppCompatActivity {

    String eventID, sJSON;
    String sSessionName, sSessionCode, sSessionUser;

    List<String> names = new ArrayList<String>();
    List<String> questions  = new ArrayList<String>();
    List<String> answers  = new ArrayList<String>();
    List<Integer> qIDs = new ArrayList<Integer>();

    TextView txtAskName, txtAskUser;
    ListView listAsk;
    MyAskAdapter adapter;

    EditText edtAskMessage;
    Button btnAskPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qna_ask);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");

        txtAskName = findViewById(R.id.txtAskName);
        txtAskUser = findViewById(R.id.txtAskUser);
        edtAskMessage = findViewById(R.id.edtAskMessage);
        btnAskPost = findViewById(R.id.btnAskPost);

        //get session info
        SendSessionReq();


        listAsk = findViewById(R.id.listAsk);

        adapter = new MyAskAdapter(this, names, questions, answers);
        listAsk.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        listAsk.invalidateViews();

        btnAskPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ask = edtAskMessage.getText().toString();
                if(!ask.equals(""))
                {
                    SendAskReq(ask);
                    Toast.makeText(QnaAsk.this, "Question added", Toast.LENGTH_SHORT).show();



                }else
                    {Toast.makeText(QnaAsk.this, "Please enter a question", Toast.LENGTH_SHORT).show();}

                adapter.notifyDataSetChanged();
            }
        });


    }

    private class MyAskAdapter extends ArrayAdapter<String> {
        Context context;
        List<String> myNames;
        List<String> myQuestions;
        List<String> myAnswers;

        MyAskAdapter(Context c, List<String> names, List<String> questions, List<String> answers)
        {
            super(c,R.layout.qansrow,R.id.qSesName, names);
            this.context=c;
            this.myNames=names;
            this.myQuestions=questions;
            this.myAnswers=answers;

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View qaskrow = inflater.inflate(R.layout.qaskrow, parent, false);
            TextView myName = qaskrow.findViewById(R.id.qAskName);
            TextView myQuestion = qaskrow.findViewById(R.id.qAskQuest);
            TextView myAnswer = qaskrow.findViewById(R.id.qAskAns);

            myName.setText(names.get(position));
            myQuestion.setText(questions.get(position));
            myAnswer.setText(answers.get(position));


            return qaskrow;
        }
    }

    //get session info
    public void SendSessionReq(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/qna/session?qnaId="+eventID;

        try{
            StringRequest objectRequest = new StringRequest(Request.Method.GET, sUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    sJSON = response.toString();
                    try{
                        // comprised of /me json response
                        JSONObject objJSON = new JSONObject(sJSON);

                        sSessionName = objJSON.get("name").toString();
                        sSessionCode = objJSON.get("eventCode").toString();
                        sSessionUser= objJSON.get("username").toString();

                        txtAskName.setText(sSessionName + " #"+sSessionCode);
                        txtAskUser.setText("by "+sSessionUser);

                        SendResponsesReq();
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
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch(Exception e){
            Log.d("HELLOOOOOOOOOOO", "EISH ");
        }

    }

    //Get all Responses
    public void SendResponsesReq(){
        names.clear();
        questions.clear();
        answers.clear();
        qIDs.clear();

        String sUrl = "https://interactlyapi.azurewebsites.net/api/qna/responses?qnaId="+eventID;

        try{
            StringRequest objectRequest = new StringRequest(Request.Method.GET, sUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    sJSON = response.toString();
                    try{
                        JSONArray jsonArray = new JSONArray(sJSON);


                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject session = jsonArray.getJSONObject(i);

                            String name;
                            if(session.isNull("userID"))
                            {
                                name = "Anonymous";
                            }else{
                                name = session.getString("userId");}

                            String question = "Q: "+session.getString("question");

                            String answer;
                            if(session.getJSONArray("replies").length()>0)
                            {
                                answer = "A: "+ session.getJSONArray("replies").getJSONObject(0).getString("answer");

                            }else
                            {
                                answer = " ";
                            }
                            int qid = session.getInt("qnaQuestionId");

                            names.add(name);
                            questions.add(question);
                            answers.add(answer);
                            qIDs.add(qid);
                        }
                        adapter.notifyDataSetChanged();

                    }
                    catch (Exception e){
                        Log.d("HELLOOOOOOOOOOO", "broke here: "+e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("HELLOOOOOOOOOOO", "OOPS ");
                }
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);



        }
        catch(Exception e){
            Log.d("HELLOOOOOOOOOOO", "EISH ");
        }

    }


    private void SendAskReq(String ask){
        String sURL = "https://interactlyapi.azurewebsites.net/api/qna/ask";

        try{

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("question",ask);
            jsonBody.put("qnaId",eventID);
            //jsonBody.put("userId",newReply);



            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, sURL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Retrieving JSON response
                    String s = response.toString();
                    try{

                        JSONObject obj = new JSONObject(s);
                    }
                    catch(Exception ex){
                        Log.d("MyFailTag", "problem: " + ex);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error Replying", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch (Exception e){
            Log.d("Uhm Yeaaaaaa", "Somethings not right: ");
        }
        SendResponsesReq();
    }
}
