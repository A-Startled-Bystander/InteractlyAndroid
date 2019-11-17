package com.example.interactly;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Survey extends AppCompatActivity {
    private String Title;
    private String Question;
    private QuestionsOptions questionsOptions;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey);

        Button btnNxtQuestion = findViewById(R.id.btnNextQuestion);
        Button btnAddOption = findViewById(R.id.btnAddOption);
        Button btnDone = findViewById(R.id.btnDone);
        final TextView txtbxQuestion = findViewById(R.id.txtbxQuestion);
        final TextView txtbxOptions = findViewById(R.id.txtbxOptions);

        final ArrayList<String> Options = new ArrayList<>();
        final ArrayList<QuestionsOptions> StoreQuestOpt = new ArrayList<QuestionsOptions>();



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

                    txtbxQuestion.setText("");
                    txtbxOptions.setText("");
                    txtbxQuestion.setEnabled(true);


                    questionsOptions = new QuestionsOptions();

                    questionsOptions.setQuestion(Question);
                    questionsOptions.setOptions(Options);
                    questionsOptions.setCount(count);
                    StoreQuestOpt.add(questionsOptions);




                for (QuestionsOptions quest: StoreQuestOpt) {
                    Log.d("READING CLASS", quest.getQuestion() + ", Options: "+ quest.getOptions() + " Count: " + count);
                }

                count = 0;

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

    public class QuestionsOptions {

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
