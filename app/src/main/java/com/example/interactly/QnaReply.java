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

public class QnaReply extends AppCompatDialogFragment {

    private QnaReplyListener listener;
    EditText edtQnaReply;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.qna_reply, null);
        edtQnaReply = view.findViewById(R.id.edtReplyMessage);


        builder.setView(view)
                .setTitle("Reply")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reply = edtQnaReply.getText().toString();
                listener.getReply(reply);
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (QnaReplyListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + "must implement QnaReply");
        }
    }

    public interface QnaReplyListener
    {
        void getReply(String qnaReply);
    }

}
