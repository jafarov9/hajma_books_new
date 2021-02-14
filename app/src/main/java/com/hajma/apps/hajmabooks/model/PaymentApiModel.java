package com.hajma.apps.hajmabooks.model;

import com.google.gson.annotations.Expose;

public class PaymentApiModel {

    @Expose
    private String status;
    @Expose
    private String error_message;

    public String getStatus() {
        return status;
    }

    public String getError_message() {
        return error_message;
    }
}
