package com.hajma.apps.hajmabooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.hajma.apps.hajmabooks.util.SimpleErrorDialog;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.StripeIntent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;


public class CheckOutActivity extends AppCompatActivity {


    private UserDAOInterface userDIF;
    private int bookId;
    private int toUserId;
    private int paidType;
    private String token;
    private SharedPreferences sharedPreferences;
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private String paymentMethodId;
    private static final String RETURN_URL = "...";
    private ProgressBar pbPaymentLoading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        pbPaymentLoading = findViewById(R.id.pbPaymentLoading);
        pbPaymentLoading.setIndeterminate(true);

        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);


        paymentMethodId = getIntent().getStringExtra("paymentMethodId");


        Log.e("payyy", paymentMethodId);

        userDIF = ApiUtils.getUserDAOInterface();


    }

    private void startCheckout() {

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bookId));

        Log.e("cncn", "ID : "+bookId);

        if(bookId > 0) {
            userDIF.createPaymentIntentSingleBook(idBody, "Bearer " + token)
                    .enqueue(new PayCallBack(this));


        }else {
            userDIF.createPaymentIntentAllBooks("Bearer " + token)
                    .enqueue(new PayCallBack(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }


    private void confirmPayment(@NonNull String clientSecret, @NonNull String paymentMethodId) {
        stripe.confirmPayment(this,
                ConfirmPaymentIntentParams.createWithPaymentMethodId(paymentMethodId, clientSecret)
        );
    }


    private static final class PayCallBack implements Callback<ResponseBody> {

        @NonNull private final WeakReference<CheckOutActivity> activityRef;

        PayCallBack(@NonNull CheckOutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }


        @Override
        public void onResponse(Call<ResponseBody> call,@NonNull final Response<ResponseBody> response) {

            final CheckOutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                try {
                    activity.onPaymentSuccess(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

            final CheckOutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + t.toString(), Toast.LENGTH_LONG
                    ).show()
            );

        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<CheckOutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckOutActivity activity) {
            activityRef = new WeakReference<>(activity);

        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckOutActivity activity = activityRef.get();


            activity.pbPaymentLoading.setIndeterminate(false);
            activity.pbPaymentLoading.setVisibility(View.GONE);

            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                /*Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent),
                        true

                );*/


                if(activity.toUserId == 0 && activity.paidType == C.PAID_TYPE_SINGLE) {
                    activity.addSingleBookToMyBooks();

                }else if(activity.paidType == C.PAID_TYPE_MULTIPLE){
                    activity.addAllBooksToMyBooks();
                }else if(activity.paidType == C.PAID_TYPE_GIFT) {
                    activity.giftBookToMyFriend();
                }


            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                String title = "Payment failed";
                String message = Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage();
                activity.openDialog(message, C.ALERT_TYPE_LOGIN_ERROR);


                /*activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
                        false
                );*/


            }
        }




        @Override
        public void onError(@NonNull Exception e) {
            final CheckOutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

    private void onPaymentSuccess(@NonNull final Response<ResponseBody> response) throws IOException {


        String s = response.body().string();

        try {
            JSONObject clientObject = new JSONObject(s);
            paymentIntentClientSecret = clientObject.getString("clientSecret");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        stripe = new Stripe(
                getApplicationContext(),
                PaymentConfiguration.getInstance(this).getPublishableKey()
        );


        confirmPayment(paymentIntentClientSecret, paymentMethodId);
    }


    private void displayAlert(@NonNull String title,
                              @Nullable String message,
                              boolean restartDemo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);



        if (restartDemo) {
            builder.setPositiveButton("Restart demo",
                    (DialogInterface dialog, int index) -> {
                        //CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                        //cardInputWidget.clear();
                        startCheckout();
                    });
        } else {
            builder.setPositiveButton("Ok", null);
        }
        builder.create().show();
    }

    //for pay single book
    private void addSingleBookToMyBooks() {

        Log.e("cncn", "Bess : "+bookId);

        RequestBody bookIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bookId));

        //not gift, for direct pay
        RequestBody toUserIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(toUserId));


        userDIF.addSingleBookToMyBooksOrGift(bookIdBody, toUserIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {

                    String message = getResources().getString(R.string._added_to_my_books);
                    openDialog(message, C.ALERT_TYPE_PAYMENT_NOTIFY);
                    EventBus.getDefault().postSticky(new DataEvent.CallProfileDetailsUpdate(1));

                }else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                String err = getResources().getString(R.string.check_your_internet_connection);
                openDialog(err, C.ALERT_TYPE_LOGIN_ERROR);

            }
        });



    }

    //for all books buy
    private void addAllBooksToMyBooks() {


        userDIF.addAllBooksToMyBooks("Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    String message = getResources().getString(R.string._added_to_my_books);
                    openDialog(message, C.ALERT_TYPE_PAYMENT_NOTIFY);
                    EventBus.getDefault().postSticky(new DataEvent.CallProfileDetailsUpdate(1));

                }else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String err = getResources().getString(R.string.check_your_internet_connection);
                openDialog(err, C.ALERT_TYPE_LOGIN_ERROR);
            }
        });


    }


    //for gift book to other user
    private void giftBookToMyFriend() {

        RequestBody bookIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bookId));

        //not gift, for direct pay
        RequestBody toUserIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(toUserId));


        userDIF.addSingleBookToMyBooksOrGift(bookIdBody, toUserIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {

                    String message = getResources().getString(R.string._present_sended);
                    openDialog(message, C.ALERT_TYPE_PAYMENT_NOTIFY);

                }else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                String err = getResources().getString(R.string.check_your_internet_connection);
                openDialog(err, C.ALERT_TYPE_LOGIN_ERROR);

            }
        });



    }

    private void openDialog(String message, int type) {
        SimpleErrorDialog errorDialog = new SimpleErrorDialog(message, type);
        errorDialog.show(getSupportFragmentManager(), "dlg");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void OnCallPaidInformation(DataEvent.CallPayInformation info) {

        Log.e("infooo", "Burdayam");

        if(info.getPaidType() == C.PAID_TYPE_SINGLE) {
            paidType = C.PAID_TYPE_SINGLE;
            bookId = info.getBookId();
            toUserId = 0;
        }else if(info.getPaidType() == C.PAID_TYPE_MULTIPLE) {
            paidType = C.PAID_TYPE_MULTIPLE;
            bookId = -1;
            toUserId = 0;
        }else if(info.getPaidType() == C.PAID_TYPE_GIFT) {
            paidType = C.PAID_TYPE_GIFT;
            bookId = info.getBookId();
            toUserId = info.getToUserId();
        }

        if(token != null) {
            startCheckout();
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}