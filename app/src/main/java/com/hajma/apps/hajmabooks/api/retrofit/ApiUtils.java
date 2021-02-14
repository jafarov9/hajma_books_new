package com.hajma.apps.hajmabooks.api.retrofit;


import com.hajma.apps.hajmabooks.C;

public class ApiUtils {

    public static UserDAOInterface getUserDAOInterface() {
        return RetrofitClient.getClient(C.BASE_URL).create(UserDAOInterface.class);
    }


}
