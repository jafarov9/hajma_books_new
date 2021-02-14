package com.hajma.apps.hajmabooks;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.hajma.apps.hajmabooks.api.retrofit.RetrofitFactory;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BookEphermenalKeyProvider implements EphemeralKeyProvider {

    private String token;
    public BookEphermenalKeyProvider(String token) {
        this.token = token;
    }

    private final UserDAOInterface backendApi =
            new RetrofitFactory(C.BASE_URL).create();
    private final CompositeDisposable compositeDisposable =
            new CompositeDisposable();

    @Override
    public void createEphemeralKey(
            @NonNull @Size(min = 4) String apiVersion,
            @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        final Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", "2019-12-03");

        // Using RxJava2 for handling asynchronous responses
        compositeDisposable.add(backendApi.getEphermenalKey(apiParamMap, "Bearer "+ token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> {
                    keyUpdateListener.onKeyUpdateFailure(0, error.getMessage());
                    Log.e("ephe", "Error");

                })
                .subscribe(
                        response -> {
                            try {
                                final String rawKey = response.string();
                                keyUpdateListener.onKeyUpdate(rawKey);

                                Log.e("ephe", "Burdayam");

                            } catch (IOException ignored) {
                                keyUpdateListener.onKeyUpdateFailure(0, Objects.requireNonNull(ignored.getMessage()));
                                Log.e("ephe", "Error");
                            }

                        }));
    }



}