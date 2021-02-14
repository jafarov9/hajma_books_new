package com.hajma.apps.hajmabooks.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.fragment.FragmentLogin;
import com.hajma.apps.hajmabooks.fragment.FragmentSignUp;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

public class LoginActivity extends AppCompatActivity {

    private ImageButton imageButtonBackFromLogin;
    private FrameLayout frm_sign_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageButtonBackFromLogin = findViewById(R.id.imageButtonBackFromLogin);

        imageButtonBackFromLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        frm_sign_container = findViewById(R.id.fragment_sign_container);
        int type = getIntent().getIntExtra("typ", 0);

        if(type == 0) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .add(R.id.fragment_sign_container, new FragmentLogin())
                    .addToBackStack("fragLogin")
                    .commit();
        }else if(type == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .add(R.id.fragment_sign_container, new FragmentSignUp())
                    .addToBackStack("fragSignUp")
                    .commit();
        }



        imageButtonBackFromLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
