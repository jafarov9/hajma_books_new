package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentForgotPassword extends Fragment {

    private EditText etForgotEmail;
    private EditText etForgotPhoneNumber;
    private Button btnResetPassword;
    private ProgressBar pbResetPasswordButtonLoading;
    private UserDAOInterface userDIF;
    private int forgotType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgo_password, container, false);

        userDIF = ApiUtils.getUserDAOInterface();


        //init variables
        etForgotEmail = view.findViewById(R.id.etForgotEmail);
        etForgotPhoneNumber = view.findViewById(R.id.etForgotPhoneNumber);
        btnResetPassword = view.findViewById(R.id.btnForgotPassword);
        pbResetPasswordButtonLoading = view.findViewById(R.id.pbResetPasswordButtonLoading);


        etForgotEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().equals("")) {
                    etForgotPhoneNumber.setEnabled(true);
                    forgotType = -1;
                }else {
                    etForgotPhoneNumber.setEnabled(false);
                    forgotType = C.FORGOT_TYPE_EMAIL;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etForgotPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")) {
                    etForgotEmail.setEnabled(true);
                    forgotType = -1;
                }else {
                    etForgotEmail.setEnabled(false);
                    forgotType = C.FORGOT_TYPE_PHONE;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextControl()) {

                    switch (forgotType) {
                        case C.FORGOT_TYPE_EMAIL :
                            resetPasswordWithEmail();
                            break;

                        case C.FORGOT_TYPE_PHONE :
                            resetPasswordWithPhone();
                            break;
                    }


                }

            }
        });



        return view;
    }

    private void resetPasswordWithPhone() {

        btnResetPassword.setVisibility(View.GONE);
        pbResetPasswordButtonLoading.setIndeterminate(true);
        pbResetPasswordButtonLoading.setVisibility(View.VISIBLE);

        String mobile = etForgotPhoneNumber.getText().toString().trim();

        userDIF.forgotPasswordWithPhone(mobile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                pbResetPasswordButtonLoading.setIndeterminate(false);
                pbResetPasswordButtonLoading.setVisibility(View.GONE);
                btnResetPassword.setVisibility(View.VISIBLE);


                if(response.isSuccessful()) {
                    try {
                        String s = response.body().string();

                        JSONObject success = new JSONObject(s).getJSONObject("success");
                        Toast.makeText(getActivity(), success.getString("message"), Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {
                        String s = response.errorBody().string();
                        JSONObject error = new JSONObject(s).getJSONObject("error");

                        Toast.makeText(getActivity(), error.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void resetPasswordWithEmail() {

        btnResetPassword.setVisibility(View.GONE);
        pbResetPasswordButtonLoading.setIndeterminate(true);
        pbResetPasswordButtonLoading.setVisibility(View.VISIBLE);

        String email = etForgotEmail.getText().toString().trim();

        Log.e("llll", email);

        userDIF.forgotPasswordWithEmail(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                pbResetPasswordButtonLoading.setIndeterminate(false);
                pbResetPasswordButtonLoading.setVisibility(View.GONE);
                btnResetPassword.setVisibility(View.VISIBLE);


                if(response.isSuccessful()) {
                    try {
                        String s = response.body().string();

                        JSONObject success = new JSONObject(s).getJSONObject("success");
                        Toast.makeText(getActivity(), success.getString("message"), Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {
                        String s = response.errorBody().string();
                        JSONObject error = new JSONObject(s).getJSONObject("error");

                        Toast.makeText(getActivity(), error.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private boolean editTextControl() {
        if(!etForgotEmail.getText().toString().trim().isEmpty() | !etForgotPhoneNumber.getText().toString().trim().isEmpty()) {
            return true;
        }else {
            Toast.makeText(getActivity(), "Choose one forgot type", Toast.LENGTH_LONG).show();
            return false;
        }

    }


}
