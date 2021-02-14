package com.hajma.apps.hajmabooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.data.AppProvider;
import com.hajma.apps.hajmabooks.model.UserApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.hajma.apps.hajmabooks.util.SimpleErrorDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 60000;
    private TextView txtCountDown;
    private CountDownTimer countDownTimer;
    private boolean mTimerIsRunning;
    private long mTimeLeftInMilllis = START_TIME_IN_MILLIS;
    private ProgressBar progressBarVerifyLoading;

    private Button btnResendSms;
    private Button btnNextToVerification;
    private Button btnCancelVerification;
    private PinView pinView;
    private String token;
    private String phone;
    private UserDAOInterface userDIF;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Uri CONTENT_URI = AppProvider.CONTENT_URI_USERS;
    private int verifyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        userDIF = ApiUtils.getUserDAOInterface();

        sharedPreferences = getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //initialize
        progressBarVerifyLoading = findViewById(R.id.progressBarVerifyLoading);
        progressBarVerifyLoading.setIndeterminate(false);


        txtCountDown = findViewById(R.id.txtCountdown);
        btnResendSms = findViewById(R.id.btnResendSms);


        verifyType = getIntent().getIntExtra("verifyType", 0);



        startTimerResendCode();

        btnResendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                startTimerResendCode();
                resendSMS();
            }
        });

        btnNextToVerification = findViewById(R.id.btnFinishVerification);
        pinView = findViewById(R.id.firstPinView);

        token = getIntent().getStringExtra("token");
        phone = getIntent().getStringExtra("phone");

        btnNextToVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNextToVerification.setVisibility(View.INVISIBLE);
                progressBarVerifyLoading.setVisibility(View.VISIBLE);
                progressBarVerifyLoading.setIndeterminate(true);

                String code = pinView.getText().toString();
                checkVerifyCodeIsCorrect(code);
            }
        });

        if(verifyType == C.VERIFY_TYPE_RESEND) {
            resendSMS();
        }

    }

    private void resendSMS() {
        userDIF.postResendSms("Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    String s = getResources().getString(R.string._new_code_sent);
                    openErrorDialog(s);
                }else {
                    String s = getResources().getString(R.string._new_code_sent);
                    openErrorDialog(s);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String s = getResources().getString(R.string.check_your_internet_connection);
                openErrorDialog(s);
            }
        });

    }

    private void openBackWarningDialog(String warningMsg) {

        SimpleErrorDialog errorDialog = new SimpleErrorDialog(warningMsg, C.ALERT_TYPE_CANCEL_VERIFY);
        errorDialog.show(getSupportFragmentManager(), "dlg");

    }

    private void openErrorDialog(String err) {
        SimpleErrorDialog errorDialog = new SimpleErrorDialog(err, C.ALERT_TYPE_LOGIN_ERROR);
        errorDialog.show(getSupportFragmentManager(), "dlg");
    }

    private boolean checkVerifyCodeIsCorrect(String code) {

        RequestBody codeBody = RequestBody.create(MediaType.parse("text/plain"), code);

        userDIF.postCodeVerify(codeBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                progressBarVerifyLoading.setVisibility(View.GONE);
                progressBarVerifyLoading.setIndeterminate(false);
                btnNextToVerification.setVisibility(View.VISIBLE);

                if(response.isSuccessful()) {

                    try {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);

                        UserApiModel user = new UserApiModel();
                        user.setEmail(jsonObject.getJSONObject("success").getString("email"));
                        user.setName(jsonObject.getJSONObject("success").getString("name"));
                        user.setMobile(jsonObject.getJSONObject("success").getString("mobile"));
                        user.setUsername(jsonObject.getJSONObject("success").getString("username"));
                        user.setVerified(jsonObject.getJSONObject("success").getBoolean("verified"));
                        user.setProfile(jsonObject.getJSONObject("success").getString("profile"));
                        token = jsonObject.getJSONObject("success").getString("token");

                        if(token != null) {
                            editor.putString("token", token);
                            editor.putBoolean("logined", true);
                            editor.commit();
                            insertUserToDatabase(user);
                            restartHome();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {
                        String errResponse = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errResponse).getJSONObject("error");
                        String errorMessage = jsonObject.getString("message");
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

                progressBarVerifyLoading.setVisibility(View.GONE);
                progressBarVerifyLoading.setIndeterminate(false);
                btnNextToVerification.setVisibility(View.VISIBLE);


                String err = getResources().getString(R.string.check_your_internet_connection);
                openErrorDialog(err);
            }
        });

        return true;
    }

    //insert user sqlite database
    public void insertUserToDatabase(UserApiModel user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());
        values.put("profile", user.getProfile());
        values.put("phone", user.getMobile());

        Uri uri = getContentResolver().insert(CONTENT_URI, values);
        editor.putString("username", user.getUsername());
        editor.commit();
    }

    public void startTimerResendCode() {
        countDownTimer = new CountDownTimer(mTimeLeftInMilllis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMilllis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerIsRunning = false;
                btnResendSms.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerIsRunning = true;
        btnResendSms.setVisibility(View.INVISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMilllis = START_TIME_IN_MILLIS;
        updateCountDownText();



    }

    @Override
    public void onBackPressed() {
        String warningMsg = getResources().getString(R.string._if_you_back);
        openBackWarningDialog(warningMsg);
    }

    private void updateCountDownText() {

        int minutes = (int) (mTimeLeftInMilllis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMilllis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        txtCountDown.setText(timeLeftFormatted);
    }

    private void restartHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
