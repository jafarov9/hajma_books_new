package com.hajma.apps.hajmabooks;

public class C {

    public static final String BASE_URL = "https://hajmabooks.com";


    public static int TYPE_FREE = 0;
    public static int TYPE_PAİD = 1;
    public static int TYPE_AUDIO = 3;
    public static int TYPE_AUTHOR = 19;
    public static int TYPE_AUDIO_FREE = 21;
    public static int TYPE_BESTSELLER = 4;
    public static int TYPE_FOR_YOU = 5;
    public static int TYPE_NEW = 6;
    public static int TYPE_SUB = 10;
    public static int TYPE_COLLECTION = 11;
    public static int TYPE_CATEGORY = 12;

    public static int TYPE_SEARCH_BOOK = 100;
    public static int TYPE_SEARCH_AUDIO_BOOK = 101;

    public static final int TYPE_ALL_AUTHOR = 21;
    public static final int TYPE_ALL_PEOPLE = 22;

    public static final int PEOPLE_TYPE_GIFT = 99;
    public static final int PEOPLE_TYPE_NORMAL = 88;

    //alert types
    public static final int ALERT_TYPE_LOGIN_ERROR = 0;
    public static final int ALERT_TYPE_CANCEL_VERIFY = 1;
    public static final int ALERT_TYPE_PAYMENT_NOTIFY = 2;
    public static final int ALERT_TYPE_NOT = 12;


    //verify type
    public static int VERIFY_TYPE_RESEND = 1;


    //PAID TYPE SINGLE AND MULTIPLE
    public static final int PAID_TYPE_SINGLE = 1;
    public static final int PAID_TYPE_MULTIPLE = 2;
    public static final int PAID_TYPE_GIFT = 3;


    //stripe publishable key
    //pk_live_I67jphvLjrRiQRMz6l5o9GYc
    //pk_test_z3c6jwuMe9E8263Jd5eXbY55

    public static String PUBLISHABLE_KEY = "pk_live_I67jphvLjrRiQRMz6l5o9GYc";


    //LANGUAGES CONSTANTS
    public static final int LANGUAGE_AZ = 1;
    public static final int LANGUAGE_EN = 2;
    public static final int LANGUAGE_RU = 3;
    public static final int LANGUAGE_TR = 4;

    //FOLLOWING TYPE
    public static final int TYPE_FOLLOWERS = 1;
    public static final int TYPE_FOLLOWINGS = 2;

    //FORGOT TYPE
    public static final int FORGOT_TYPE_EMAIL = 1;
    public static final int FORGOT_TYPE_PHONE = 2;

    public static final int MY_BOOK = 1;
    public static final int OTHER_BOOK = 2;

}
