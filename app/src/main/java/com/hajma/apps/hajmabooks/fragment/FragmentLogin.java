package com.hajma.apps.hajmabooks.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.activity.VerificationActivity;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.data.AppProvider;
import com.hajma.apps.hajmabooks.model.UserApiModel;
import com.hajma.apps.hajmabooks.util.SimpleErrorDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentLogin extends Fragment {

    private EditText etLoginUsernameEmail;
    private EditText etLoginPassword;
    private Button btnLogin;
    private TextView txtSignUpFromLogin;
    private FragmentManager fm;
    private String token;
    private UserDAOInterface userDIF;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Uri CONTENT_URI = AppProvider.CONTENT_URI_USERS;
    private ProgressBar pBLoginLoading;
    private TextView txtForgotPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fm = getActivity().getSupportFragmentManager();

        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userDIF = ApiUtils.getUserDAOInterface();


        pBLoginLoading = view.findViewById(R.id.progressBarLoginButtonLoading);


        txtSignUpFromLogin = view.findViewById(R.id.txtSignUpFromLogin);
        txtSignUpFromLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_sign_container, new FragmentSignUp())
                        .addToBackStack("fragSignUp")
                        .commit();
            }
        });


        txtForgotPassword = view.findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_sign_container, new FragmentForgotPassword())
                        .addToBackStack("fragForgotPassword")
                        .commit();
            }
        });

        etLoginUsernameEmail = view.findViewById(R.id.etLoginUsernameEmail);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postLogin();
            }
        });


        return view;
    }

    //Post login
    private void postLogin() {

        btnLogin.setVisibility(View.INVISIBLE);
        pBLoginLoading.setVisibility(View.VISIBLE);
        pBLoginLoading.setIndeterminate(true);

        String email = etLoginUsernameEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);

        userDIF.postLogin(emailBody, passwordBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("zczc", "onresponse evveli");

                pBLoginLoading.setVisibility(View.GONE);
                pBLoginLoading.setIndeterminate(false);
                btnLogin.setVisibility(View.VISIBLE);

                if(response.isSuccessful()) {

                    Log.e("zczc", "onresponse success");

                    try {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);

                        UserApiModel user = new UserApiModel();
                        user.setEmail(jsonObject.getJSONObject("success").getJSONObject("user").getString("email"));
                        user.setName(jsonObject.getJSONObject("success").getJSONObject("user").getString("name"));
                        user.setUsername(jsonObject.getJSONObject("success").getJSONObject("user").getString("username"));
                        user.setVerified(jsonObject.getJSONObject("success").getJSONObject("user").getBoolean("verified"));
                        user.setProfile(jsonObject.getJSONObject("success").getJSONObject("user").getString("profile"));
                        token = jsonObject.getJSONObject("success").getString("token");

                        Log.e("zczc", "ssd: "+user.isVerified());


                        if(token != null) {
                            if(user.isVerified()) {

                                editor.putString("token", token);
                                editor.putBoolean("logined", true);
                                editor.commit();

                                restartHome();
                            }else {
                                Intent intent = new Intent(getActivity(), VerificationActivity.class);
                                intent.putExtra("token", token);
                                intent.putExtra("verifyType", C.VERIFY_TYPE_RESEND);
                                startActivity(intent);
                            }
                        }



                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    String err = getResources().getString(R.string._you_are_not_registered);
                    openErrorDialog(err);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                pBLoginLoading.setVisibility(View.GONE);
                pBLoginLoading.setIndeterminate(false);
                btnLogin.setVisibility(View.VISIBLE);

                String err = getActivity().getResources().getString(R.string.check_your_internet_connection);
                openErrorDialog(err);
            }
        });

    }

    private void restartHome() {
        Log.e("zczc", "restart home");
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private boolean editTextControl() {
        if(etLoginUsernameEmail.getText().toString().trim().isEmpty()) {
            etLoginUsernameEmail.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etLoginPassword.getText().toString().trim().isEmpty()) {
            etLoginPassword.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        return true;
    }


    //check user is exists database
    /*public boolean  userIsExistsInDatabase(String username) {

        Cursor cursor;

        String projection[] = {"username"};
        String selection = "username = ?";
        String selectionArgs[] = {username};
        cursor = getActivity().getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, null);

        if(cursor != null && cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                editor.putString("username", cursor.getString(cursor.getColumnIndex("username")));
                editor.commit();
                return true;
            }
        }
        return false;
    }*/

    //insert user sqlite database
    /*public void insertUserToDatabase(UserApiModel user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());
        values.put("profile", user.getProfile());
        values.put("phone", user.getMobile());

        Uri uri = getActivity().getContentResolver().insert(CONTENT_URI, values);
        editor.putString("username", user.getUsername());
        editor.commit();
    }*/

    private void openErrorDialog(String message) {
        SimpleErrorDialog errorDialog = new SimpleErrorDialog(message, C.ALERT_TYPE_LOGIN_ERROR);
        errorDialog.show(getFragmentManager(), "dlg");
    }

}
