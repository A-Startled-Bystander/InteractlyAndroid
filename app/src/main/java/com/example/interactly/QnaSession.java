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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QnaSession extends AppCompatActivity {

    String sToken, user, qnaID;
    String sJSON, sSessionName, sSessionUser, sSessionCode;

    List<String> names = new ArrayList<String>();
    List<String> questions  = new ArrayList<String>();
    List<String> answers  = new ArrayList<String>();

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
        user = intent.getStringExtra("user");
        qnaID = intent.getStringExtra("qnaID");

        sSesName = findViewById(R.id.txtSessionName);
        sSesUser = findViewById(R.id.txtSessionUser);


        SendSessionReq();
        SendResponsesReq();

        listSession = findViewById(R.id.listSession);

        adapter = new MySesAdapter(this, names, questions, answers );
        listSession.setAdapter(adapter);

        //get session
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
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View qansrow = inflater.inflate(R.layout.qansrow, parent, false);
            TextView myName = qansrow.findViewById(R.id.qSesName);
            TextView myQuestion = qansrow.findViewById(R.id.qSesQuest);
            TextView myAnswer = qansrow.findViewById(R.id.qSesAns);
            myName.setText(names.get(position));
            myQuestion.setText(questions.get(position));
            myAnswer.setText(answers.get(position));

            return qansrow;
        }
    }
    public class ViewHolder{
        TextView name;
        TextView question;
        TextView answer;
        Button button;
    }

    //Get all Responses
    public void SendResponsesReq(){
        names.clear();
        questions.clear();
        answers.clear();

        String sUrl = "https://interactlyapi.azurewebsites.net/api/qna/responses?qnaId="+qnaID;
        Toast.makeText(this, "made it here atleasr", Toast.LENGTH_SHORT).show();

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

                            String name = "test";
                            //if(session.getString("userID").equals(null))
                            //{
                            //    name = "Anonymous";
                            //}else{
                            //    name = session.getString("userId");}

                            String question = session.getString("question");
                            String answer = session.getJSONArray("replies").getJSONObject(0).getString("answer");

                            Toast.makeText(QnaSession.this, "Test: "+name+question+answer, Toast.LENGTH_SHORT).show();


                            names.add(name);
                            questions.add(question);
                            answers.add(answer);
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
}
