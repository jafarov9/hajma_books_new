package com.hajma.apps.hajmabooks.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.Stripe;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.model.Token;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class PaymentMethodActivity extends AppCompatActivity {

    private CardInputWidget cardInputView;
    private Button btnEndPay;
    private UserDAOInterface userDIF;
    private String token;
    private SharedPreferences sharedPreferences;
    private Stripe stripe;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        WeakReference<PaymentMethodActivity> weakActivity = new WeakReference<>(this);

        userDIF = ApiUtils.getUserDAOInterface();
        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        //init variables
        cardInputView = findViewById(R.id.cardInputView);
        btnEndPay = findViewById(R.id.btnEndPay);

        btnEndPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the cardInputView details from the cardInputView widget
                Card card = cardInputView.getCard();

                if(card != null) {
                    // Create a Stripe token from the card details
                    stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
                    stripe.createToken(card, new ApiResultCallback<Token>() {



                    @Override
                    public void onSuccess(@NonNull Token result) {
                        String tokenID = result.getId();
                        // Send the token identifier to the server...
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // Handle error
                    }


                    });
                }


            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
