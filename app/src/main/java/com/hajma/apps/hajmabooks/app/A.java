package com.hajma.apps.hajmabooks.app;


import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.util.CheckNetwork;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.stripe.android.PaymentConfiguration;

public class A extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


        CheckNetwork checkNetwork = new CheckNetwork(getApplicationContext());
        checkNetwork.registerNetworkCallback();


        Log.e("ppp", "BNurdasd");

            PaymentConfiguration.init(
                    getApplicationContext(),
                    C.PUBLISHABLE_KEY
            );
    }


}
