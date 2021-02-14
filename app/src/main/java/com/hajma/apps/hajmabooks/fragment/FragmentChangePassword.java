package com.hajma.apps.hajmabooks.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentChangePassword extends Fragment {

    private EditText etChangePasswordOld;
    private EditText etChangePasswordNew;
    private Button btnCPassword;
    private String token;
    private UserDAOInterface userDIF;
    private ProgressBar pbChangePassword;
    private ImageButton imgBtnBackChangePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        userDIF = ApiUtils.getUserDAOInterface();

        token = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE).getString("token", null);


        //init variables
        pbChangePassword = view.findViewById(R.id.pbChangePassword);
        etChangePasswordOld = view.findViewById(R.id.etChangePasswordOld);
        etChangePasswordNew = view.findViewById(R.id.etChangePasswordNew);
        imgBtnBackChangePassword = view.findViewById(R.id.imgBtnBackChangePassword);



        btnCPassword = view.findViewById(R.id.btnCPassword);

        btnCPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etLengthControl()) {
                    if (editTextControl()) {
                        changePassword();
                    }
                }
            }
        });

        imgBtnBackChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void changePassword() {

        btnCPassword.setVisibility(View.GONE);
        pbChangePassword.setIndeterminate(true);
        pbChangePassword.setVisibility(View.VISIBLE);

        String oldPassword = etChangePasswordOld.getText().toString().trim();
        String newPassword = etChangePasswordNew.getText().toString().trim();

        RequestBody oldBody = RequestBody.create(MediaType.parse("text/plain"), oldPassword);
        RequestBody newBody = RequestBody.create(MediaType.parse("text/plain"), newPassword);

        userDIF.postChangePassword(newBody, oldBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                pbChangePassword.setIndeterminate(false);
                pbChangePassword.setVisibility(View.GONE);
                btnCPassword.setVisibility(View.VISIBLE);

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
        if(etChangePasswordOld.getText().toString().trim().isEmpty()) {
            etChangePasswordOld.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etChangePasswordNew.getText().toString().trim().isEmpty()) {
            etChangePasswordNew.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        return true;
    }

    private boolean etLengthControl() {

        if(etChangePasswordOld.getText().toString().length() < 8 | etChangePasswordNew.getText().toString().length() < 8) {
            Toast.makeText(getActivity(), "Minumum 8 character", Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }

    }

}
