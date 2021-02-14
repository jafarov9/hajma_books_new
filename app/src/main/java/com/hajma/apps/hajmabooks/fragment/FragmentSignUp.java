package com.hajma.apps.hajmabooks.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.VerificationActivity;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.data.AppProvider;
import com.hajma.apps.hajmabooks.model.UserApiModel;
import com.hajma.apps.hajmabooks.util.SimpleErrorDialog;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSignUp extends Fragment {

    private CountryCodePicker ccp;
    private EditText etSignUpPhoneNumber;
    private Button btnRegister;
    private TextView txtLoginFromSignUp;
    private EditText etSignUpName;
    private EditText etsignUpPassword;
    private EditText etSignUpPasswordTwo;
    private EditText etSignUpEmail;
    private EditText etSignUpUsername;
    private ProgressBar progressBarRegisterLoading;
    private UserDAOInterface userDIF;
    private Uri CONTENT_URI = AppProvider.CONTENT_URI_USERS;
    private String token;
    private String mobile;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //init variables
        progressBarRegisterLoading = view.findViewById(R.id.progressBarRegisterLoading);


        btnRegister = view.findViewById(R.id.btnRegister);
        etSignUpName = view.findViewById(R.id.etSignUpName);
        etSignUpUsername = view.findViewById(R.id.etSignUpUsername);
        etSignUpEmail = view.findViewById(R.id.etEmailSignUp);
        etsignUpPassword = view.findViewById(R.id.etSignUpPassword);
        etSignUpPasswordTwo = view.findViewById(R.id.etSignUpPasswordTwo);
        userDIF = ApiUtils.getUserDAOInterface();


        txtLoginFromSignUp = view.findViewById(R.id.txtLoginFromSignUp);

        txtLoginFromSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_sign_container, new FragmentLogin())
                        .addToBackStack("fragLogin")
                        .commit();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextControl()) {
                    if(passwordContainControl()) {
                        if(passwordsEqualControl()) {
                            attemptRegister();
                        }
                    }
                }
            }
        });

        ccp = view.findViewById(R.id.ccp);
        ccp.setCountryForPhoneCode(994);
        etSignUpPhoneNumber = view.findViewById(R.id.etSignUpPhoneNumber);
        etSignUpPhoneNumber.setHint("70 911 23 11");

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                switch (ccp.getSelectedCountryCodeAsInt()) {
                    case 994 :
                        etSignUpPhoneNumber.setHint("70 911 23 11");
                    break;

                    case 7 :
                        etSignUpPhoneNumber.setHint("495 123 4567");
                        break;

                    case 90 :
                        etSignUpPhoneNumber.setHint("216 555 55 55");
                        break;

                    default: etSignUpPhoneNumber.setHint("Phone");
                }
            }
        });

        return view;
    }

    private void attemptRegister() {

        //hide register button
        btnRegister.setVisibility(View.INVISIBLE);
        //show progress bar loading register
        progressBarRegisterLoading.setVisibility(View.VISIBLE);
        progressBarRegisterLoading.setIndeterminate(true);


        String email = etSignUpEmail.getText().toString().trim().replaceAll(" ", "");
        String username = etSignUpUsername.getText().toString().trim().replaceAll(" ", "");
        String name = etSignUpName.getText().toString().trim();
        String password = etsignUpPassword.getText().toString().trim();
        String c_password = etSignUpPasswordTwo.getText().toString().trim();
        String phone = ccp.getSelectedCountryCode() + etSignUpPhoneNumber.getText().toString().trim().replaceAll(" ", "");

        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody c_passwordBody = RequestBody.create(MediaType.parse("text/plain"), c_password);
        RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), phone);


        userDIF.postRegister(emailBody , usernameBody, nameBody, passwordBody, c_passwordBody, phoneBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {

                    progressBarRegisterLoading.setVisibility(View.GONE);
                    progressBarRegisterLoading.setIndeterminate(false);
                    btnRegister.setVisibility(View.VISIBLE);


                    try {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);

                        //UserApiModel user = new UserApiModel();
                        //user.setEmail(jsonObject.getJSONObject("success").getString("email"));
                        //user.setName(jsonObject.getJSONObject("success").getString("name"));
                        //user.setMobile(jsonObject.getJSONObject("success").getString("mobile"));
                        //user.setUsername(jsonObject.getJSONObject("success").getString("username"));
                        //user.setVerified(jsonObject.getJSONObject("success").getBoolean("verified"));
                        //user.setProfile(jsonObject.getJSONObject("success").getString("profile"));
                        mobile = jsonObject.getJSONObject("success").getString("mobile");
                        token = jsonObject.getJSONObject("success").getString("token");

                        if(token != null) {
                            Intent intent = new Intent(getActivity(), VerificationActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("phone", mobile);
                            startActivity(intent);
                            getActivity().finish();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    progressBarRegisterLoading.setVisibility(View.GONE);
                    progressBarRegisterLoading.setIndeterminate(false);
                    btnRegister.setVisibility(View.VISIBLE);


                    try {
                        String s = response.errorBody().string();

                        JSONObject errObject = new JSONObject(s).getJSONObject("error");
                        Iterator<String> iter = errObject.keys();
                        String key = iter.next();

                        JSONArray keyObjArray = errObject.getJSONArray(key);
                        String errorMessage = (String) keyObjArray.get(0);

                        openErrorDialog(errorMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                progressBarRegisterLoading.setVisibility(View.GONE);
                progressBarRegisterLoading.setIndeterminate(false);
                btnRegister.setVisibility(View.VISIBLE);


                String err = getActivity().getResources().getString(R.string.check_your_internet_connection);
                openErrorDialog(err);
            }
        });
    }

    private void openErrorDialog(String message) {

        SimpleErrorDialog errorDialog = new SimpleErrorDialog(message, C.ALERT_TYPE_LOGIN_ERROR);
        errorDialog.show(getFragmentManager(), "dlg");


    }

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

    private boolean editTextControl() {
        if(etSignUpEmail.getText().toString().trim().isEmpty()) {
            etSignUpEmail.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etSignUpUsername.getText().toString().trim().isEmpty()) {
            etSignUpUsername.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etSignUpName.getText().toString().trim().isEmpty()) {
            etSignUpName.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etsignUpPassword.getText().toString().trim().isEmpty()) {
            etsignUpPassword.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etSignUpPasswordTwo.getText().toString().trim().isEmpty()) {
            etSignUpPasswordTwo.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        if(etSignUpPhoneNumber.getText().toString().trim().isEmpty()) {
            etSignUpPhoneNumber.setError(getActivity().getResources().getString(R.string._required_field));
            return false;
        }

        return true;
    }

    private boolean passwordsEqualControl() {
        String p1 = etsignUpPassword.getText().toString().trim();
        String p2 = etSignUpPasswordTwo.getText().toString().trim();

        if(p1.equals(p2)) {
            return true;
        }else {
            etSignUpPasswordTwo.setError(getActivity().getResources().getString(R.string._pass_equal));
            return false;
        }
    }

    private boolean passwordContainControl() {
        if(etsignUpPassword.getText().toString().trim().contains(" ")) {
            etsignUpPassword.setError(getActivity().getResources().getString(R.string._cannot_contain_spaces));
            return false;
        }else {
            return true;
        }
    }

}
