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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Survey extends AppCompatActivity {
    private String Title;
    private String Question;
    String sToken, sJSON, user;
    private QuestionsOptions questionsOptions;
    private int count = 0;

    final private ArrayList<String> Options = new ArrayList<>();
    final private ArrayList<QuestionsOptions> StoreQuestOpt = new ArrayList<QuestionsOptions>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey);

        //-- Get JWT Token
        Intent intent = getIntent();
        sToken = intent.getStringExtra("token");
        SendUserDetailsReq();

        Button btnNxtQuestion = findViewById(R.id.btnNextQuestionP);
        Button btnAddOption = findViewById(R.id.btnAddOptionP);
        Button btnDone = findViewById(R.id.btnDoneP);
        final TextView txtbxQuestion = findViewById(R.id.txtbxQuestion);
        final TextView txtbxOptions = findViewById(R.id.txtbxOptionsp);




        //Prompt for test name
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

                String[] OptionConvert = new String[Options.size()];

                    txtbxQuestion.setText("");
                    txtbxOptions.setText("");
                    txtbxQuestion.setEnabled(true);


                for (int i = 0; i < Options.size(); i++) {

                    OptionConvert[i] = Options.get(i);
                    Log.d("String", Options.get(i));
                }

                    questionsOptions = new QuestionsOptions();

                    questionsOptions.setQuestion(Question);
                    questionsOptions.setOptions(OptionConvert);
                    StoreQuestOpt.add(questionsOptions);




                for (QuestionsOptions quest: StoreQuestOpt) {
                    String[] nerd = quest.getOptions();
                    for (int i = 0; i < nerd.length; i++) {
                        Log.d("READING CLASS", quest.getQuestion() + ", Options: "+ nerd[i]);
                    }
                }
                Options.clear();
            }
        });


    btnDone.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreateSurveyReq();
        }
    });





    }

    //test name dialog
    private void NameAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your survey name");

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

    private void CreateSurveyReq(){

        String sURL = "https://interactlyapi.azurewebsites.net/api/surveys/create";

        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title",Title);
            jsonBody.put("userId",user);
            JSONArray arr = new JSONArray();
            for (QuestionsOptions questions: StoreQuestOpt)
            {
                JSONObject objQuest = new JSONObject();
                objQuest.put("question", questions.getQuestion());
                objQuest.put("userId", user);

                JSONArray subArr = new JSONArray();
                for(String option : questions.Options)
                {
                    JSONObject objOpt = new JSONObject();
                    objOpt.put("optionText", option);
                    subArr.put(objOpt);
                }
                objQuest.put("options",subArr);
                arr.put(objQuest);
                Log.d("Jaime Test", "SubArr: "+subArr);

            }
            jsonBody.put("questions",arr);


            Log.d("Jaime Test", "CreateSurveyReq: "+jsonBody);
            Log.d("Jaime Test", "Arr: "+arr);
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

    public class QuestionsOptions {

        String Question;
        String[] Options ;

        //QuestionsOptions(String Question, ArrayList<String> options){
        //     this.Question = Question;
        //     this.options = options;
        // }

        public String getQuestion() {
            return Question;
        }

        public void setQuestion(String question) {
            this.Question = question;
        }

        public String[] getOptions() {
            return Options;
        }

        public void setOptions(String[] options) {
            this.Options = options;
        }
    }





}
