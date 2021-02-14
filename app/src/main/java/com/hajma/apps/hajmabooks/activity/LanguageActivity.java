package com.hajma.apps.hajmabooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hajma.apps.hajmabooks.MainActivity;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAZ, btnEN, btnRU;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        btnAZ = findViewById(R.id.btnAZ);
        btnEN = findViewById(R.id.btnEN);
        btnRU = findViewById(R.id.btnRU);

        btnRU.setOnClickListener(this);
        btnEN.setOnClickListener(this);
        btnAZ.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnAZ) {
            LocaleHelper.setLocale(this, "az");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        if(v.getId() == R.id.btnEN) {
            LocaleHelper.setLocale(this, "en");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        if(v.getId() == R.id.btnRU) {
            LocaleHelper.setLocale(this, "ru");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
