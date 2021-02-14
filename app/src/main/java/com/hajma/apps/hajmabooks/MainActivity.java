package com.hajma.apps.hajmabooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.activity.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSplashSignUp;
    private Button btnSplashLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSplashLogin = findViewById(R.id.btnLoginSplash);
        btnSplashSignUp = findViewById(R.id.btnSignUpSplash);

        btnSplashLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("typ", 0);
                startActivity(intent);
            }
        });

        btnSplashSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("typ", 1);
                startActivity(intent);
            }
        });


    }
}
