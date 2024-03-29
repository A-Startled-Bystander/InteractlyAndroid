package com.example.interactly;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class HostQna extends AppCompatDialogFragment {
    private EditText hQnaName;
    private HostQnaListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.host_qna, null);

        builder.setView(view)
                .setTitle("Host QnA")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = hQnaName.getText().toString();
                listener.getQnaTitle(title);
            }
        });

        hQnaName = view.findViewById(R.id.hQnaName);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (HostQnaListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "must implement HostQnaListener");
        }
    }

    public interface HostQnaListener
    {
        void getQnaTitle(String qnaTitle);
    }



}
