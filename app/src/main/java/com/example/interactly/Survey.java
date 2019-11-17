package com.example.interactly;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private String Title = "";
    private String Question;

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


        //Prompt for test name
        NameAlert();

        btnAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtbxOptions.getText().toString().equals("")|| txtbxOptions.getText().toString().equals(null)||txtbxQuestion.getText().toString().equals("")|| txtbxQuestion.getText().toString().equals(null)){

                    Toast.makeText(getApplicationContext(), "a question and option is required",Toast.LENGTH_LONG).show();
                }else{

                    Options.add(txtbxOptions.getText().toString());
                    txtbxQuestion.setEnabled(false);

                    txtbxOptions.setText("");
                }


                for (String option : Options){
                    Log.d("PLEASE",option);
                }

            }
        });
        btnNxtQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Question = txtbxQuestion.getText().toString();
                    if (txtbxOptions.getText().toString().equals("")|| txtbxOptions.getText().toString().equals(null)){

                    }else{
                        Options.add(txtbxOptions.toString());
                    }
                    txtbxQuestion.setText("");
                    txtbxQuestion.setEnabled(true);

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



}
