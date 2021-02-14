package com.hajma.apps.hajmabooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    private boolean logined;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private int bookId = 0;
    private String key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        logined = sharedPreferences.getBoolean("logined", false);

        //int bookId = 0;
        //String key = "";

        if(getIntent() != null) {
            bookId = getIntent().getIntExtra("bookIdSplash", 0);
            key = getIntent().getStringExtra("keySplash");
        }
        //Toast.makeText(this, "Book: "+bookId, Toast.LENGTH_LONG).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(logined) {
                    Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    mainIntent.putExtra("bookId", bookId);
                    mainIntent.putExtra("key", key);
                    startActivity(mainIntent);
                    finish();
                }else {
                    Intent mainIntent = new Intent(SplashActivity.this, LanguageActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            String extras = intent.getStringExtra("keySplash");
            int bookId = intent.getIntExtra("bookIdSplash", 0);

            if(extras != null && extras.equals("dtFrag")) {

                if(logined) {
                    Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    mainIntent.putExtra("bookId", bookId);
                    mainIntent.putExtra("key", key);
                    startActivity(mainIntent);
                    finish();
                }else {
                    Intent mainIntent = new Intent(SplashActivity.this, LanguageActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
