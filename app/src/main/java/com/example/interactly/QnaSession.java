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

public class QnaSession extends AppCompatActivity implements QnaReply.QnaReplyListener {

    String sToken, qnaID;
    String sJSON, sSessionName, sSessionUser, sSessionCode;
    String newReply;
    int iClicked;

    List<String> names = new ArrayList<String>();
    List<String> questions  = new ArrayList<String>();
    List<String> answers  = new ArrayList<String>();
    List<Integer> qIDs = new ArrayList<Integer>();

    TextView sSesName, sSesUser;
    ListView listSession;
    MySesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qna_session);

        //-- Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");
        qnaID = intent.getStringExtra("qnaID");

        sSesName = findViewById(R.id.txtSessionName);
        sSesUser = findViewById(R.id.txtSessionUser);


        SendSessionReq();


        listSession = findViewById(R.id.listSession);

        adapter = new MySesAdapter(this, names, questions, answers );
        listSession.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        //get session
    }

    @Override
    public void getReply(String qnaReply) {
        newReply = qnaReply;
        //Accept button for reply here
        Toast.makeText(this, "ID: "+qIDs.get(iClicked), Toast.LENGTH_SHORT).show();
        SendAnswerReq();
        //to refresh list
        SendResponsesReq();
        adapter.notifyDataSetChanged();

        //TODO: testREFRESH
        listSession.invalidateViews();



    }

    private class MySesAdapter extends ArrayAdapter<String> {
        Context context;
        List<String> myNames;
        List<String> myQuestions;
        List<String> myAnswers;

        MySesAdapter(Context c, List<String> names, List<String> questions, List<String> answers)
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
            View qansrow = inflater.inflate(R.layout.qansrow, parent, false);
            TextView myName = qansrow.findViewById(R.id.qSesName);
            TextView myQuestion = qansrow.findViewById(R.id.qSesQuest);
            TextView myAnswer = qansrow.findViewById(R.id.qSesAns);
            Button btnAnswer = qansrow.findViewById(R.id.btnSesAnswer);
            btnAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog();
                    iClicked = position;
                }
            });
            myName.setText(names.get(position));
            myQuestion.setText(questions.get(position));
            myAnswer.setText(answers.get(position));

            if(!myAnswer.getText().equals("Reply. . ."))
            {
                btnAnswer.setVisibility(View.GONE);
            }
            return qansrow;
        }
    }
    public void openDialog()
    {
        QnaReply qnaReply = new QnaReply();
        qnaReply.show(getSupportFragmentManager(), "Reply to message");


    }

    //Get all Responses
    public void SendResponsesReq(){
        names.clear();
        questions.clear();
        answers.clear();
        qIDs.clear();

        String sUrl = "https://interactlyapi.azurewebsites.net/api/qna/responses?qnaId="+qnaID;

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
                                answer = "Reply. . .";
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

    //get session info
    public void SendSessionReq(){
        String sUrl = "https://interactlyapi.azurewebsites.net/api/qna/session?qnaId="+qnaID;

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

                        sSesName.setText(sSessionName + " #"+sSessionCode);
                        sSesUser.setText("by "+sSessionUser);

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



    private void SendAnswerReq(){
        String sURL = "https://interactlyapi.azurewebsites.net/api/qna/answer";

        try{

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("answer",newReply);
            jsonBody.put("qnaQuestionId",qIDs.get(iClicked).toString());
           // jsonBody.put("userId",x);



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
}
