package com.example.interactly;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatePolls extends AppCompatActivity
{
    private String sToken,sUrl;
    private String Title;
    private String Question;
    private PollOptions pollOptions;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_polls);

        //-- Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");
        Log.d("JWT token", "onCreate: " + sToken);

        Button btnNxtQuestion = findViewById(R.id.btnNextQuestionP);
        Button btnAddOption = findViewById(R.id.btnAddOptionP);
        Button btnDone = findViewById(R.id.btnDoneP);
        final TextView txtbxQuestion = findViewById(R.id.txtbxQuestion);
        final TextView txtbxOptions = findViewById(R.id.txtbxOptionsp);
        final TextView txtbxOptions1 = findViewById(R.id.txtbxOptions1p);

        final ArrayList<String> Options = new ArrayList<>();
        final ArrayList<PollOptions> StorePollOpt = new ArrayList<CreatePolls.PollOptions>();




        NameAlert();

        btnAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtbxOptions.getText().toString().equals("")|| txtbxOptions.getText().toString().equals(null)||txtbxQuestion.getText().toString().equals("")|| txtbxQuestion.getText().toString().equals(null)){

                    Toast.makeText(getApplicationContext(), "a question and option is required",Toast.LENGTH_LONG).show();
                }else{

                    Options.add(txtbxOptions.getText().toString());
                    count++;
                    txtbxQuestion.setEnabled(false);

                    txtbxOptions.setText("");
                }


                for (String option : Options){
                    Log.d("PLEASE",option);
                }

            }
        });
        btnNxtQuestion.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                Question = txtbxQuestion.getText().toString();


                if (txtbxOptions.getText().toString().equals("")|| txtbxOptions.getText().toString().equals(null)){

                }else{
                    Options.add(txtbxOptions.getText().toString());
                }

                txtbxQuestion.setText("");
                txtbxOptions.setText("");
                txtbxQuestion.setEnabled(true);


                pollOptions = new PollOptions();

                pollOptions.setQuestion(Question);
                pollOptions.setOptions(Options);
                pollOptions.setCount(count);
                StorePollOpt.add(pollOptions);






//                for (PollOptions poll: StorePollOpt) {
//                    Log.d("READING CLASS", poll.getQuestion() + ", Options: "+ poll.getOptions() + " Count: " + count);
//                }
//
//                count = 0;

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               // createPollRequest();
            }
        });



    }

    protected void createPollRequest()
    {
        sUrl = "https://interactlyapi.azurewebsites.net/api/polls/create";

        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("pName",Title);
            jsonBody.put("question",Question);


            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, sUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast toast = Toast.makeText(getApplicationContext(),"You have successfully created a Poll", Toast.LENGTH_SHORT);
                    toast.show();

                    // Retrieving JSON response
                    String s = response.toString();
                    try{

                        JSONObject obj = new JSONObject(s);
                        Log.d("WE CONVERTED IT: ", "Username and password: " + obj.get("username"));
                    }
                    catch(Exception ex){
                        Log.d("MyFailTag", "Failed to convert " + ex);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error creating new poll", Toast.LENGTH_SHORT);
                    toast.show();
                }
            })
            {
                // Add JWT Token to the request header
                @Override
                public Map getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer "+ sToken);
                    return params;
                }

            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(objectRequest);

        }
        catch (Exception e){
            Log.d("Error", "Something went wrong: "+e.getMessage());
        }

    }

    //test name dialog
    private void NameAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your poll name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Title = input.getText().toString();
                if (Title.equals("")|| Title.equals(null)){
                    NameAlert();
                    Toast.makeText(getApplicationContext(), "a name is required!",Toast.LENGTH_LONG).show();
                }
                else{
                    dialog.cancel();
                }
            }
        });


        builder.show();
    }

    public class PollOptions {

        String Question;
        ArrayList<String> Options ;
        int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
//QuestionsOptions(String Question, ArrayList<String> options){
        //     this.Question = Question;
        //     this.options = options;
        //  }

        public String getQuestion() {
            return Question;
        }

        public void setQuestion(String question) {
            Question = question;
        }

        public ArrayList<String> getOptions() {
            return Options;
        }

        public void setOptions(ArrayList<String> options) {
            Options = options;
        }
    }




}

