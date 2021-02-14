package com.hajma.apps.hajmabooks.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.CheckOutActivity;

public class SimpleErrorDialog extends AppCompatDialogFragment {

    private String message;
    private int type;

    public SimpleErrorDialog(String message, int type) {
        this.message = message;
        this.type = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(type == C.ALERT_TYPE_LOGIN_ERROR) {
            builder.setTitle(getActivity().getResources().getString(R.string._title_error));
            builder.setMessage(message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
        } else if(type == C.ALERT_TYPE_NOT) {
            builder.setTitle("Not");
            builder.setMessage(message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
        } else if(type == C.ALERT_TYPE_PAYMENT_NOTIFY) {

            builder.setTitle(getActivity().getResources().getString(R.string._raport));
            builder.setMessage(message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            getActivity().onBackPressed();
                        }
                    });

        } else if(type == C.ALERT_TYPE_CANCEL_VERIFY) {
            builder.setTitle(getActivity().getResources().getString(R.string._attention));
            builder.setMessage(message)
                    .setPositiveButton(getActivity().getResources().getString(R.string._done), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
            builder.setNegativeButton(getActivity().getResources().getString(R.string._cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        }

        return builder.create();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
