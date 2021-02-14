package com.hajma.apps.hajmabooks.api.retrofit;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserDAOInterface {

    //Detailed book method
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/get-book-detailed")
    Call<ResponseBody> postDetailedBook(@Part("languageId") RequestBody langID,
                                        @Part("bookId") RequestBody bookID,
                                        @Header("Authorization") String token);


    //register method
    @Multipart
    @POST("/api/register")
    Call<ResponseBody> postRegister(@Part("email") RequestBody email,
                                    @Part("username") RequestBody username,
                                    @Part("name") RequestBody name,
                                    @Part("password") RequestBody password,
                                    @Part("c_password") RequestBody c_password,
                                    @Part("mobile") RequestBody mobile);

    //login method
    @Multipart
    @POST("/api/login")
    Call<ResponseBody> postLogin(@Part("email") RequestBody email,
                                 @Part("password") RequestBody password);


    /* card operations
    * remove, add, my card list */

    //list my card method
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/list-my-card")
    Call<ResponseBody> postListMyCart(@Part("languageId") RequestBody languageId, @Header("Authorization") String token);

    //remove from card
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/remove-from-card")
    Call<ResponseBody> postRemoveMyCard(@Part("book_id") RequestBody book_id,
                                        @Header("Authorization") String token);

    //add to card
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/add-to-card")
    Call<ResponseBody> postAddToCard(@Part("book_id") RequestBody book_id,
                                     @Header("Authorization") String token);




    //logout method
    @Headers({"Accept: application/json"})
    @POST("/api/logout")
    Call<ResponseBody> postLogout(@Header("Authorization") String token);

    //profile method
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/my-profile")
    Call<ResponseBody> postMyProfile(@Part("languageId") RequestBody languageId, @Header("Authorization") String token);

    //Other profile method
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/user-profile")
    Call<ResponseBody> postOtherProfile(@Part("languageId") RequestBody languageId,
                                        @Part("userId") RequestBody userId,
                                        @Header("Authorization") String token);

    //send follow request
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/send-follow-request")
    Call<ResponseBody> postSendFollowRequest(@Part("toUserId") RequestBody toUserId,
                                             @Header("Authorization") String token);

    //unfollow request
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/unfollow")
    Call<ResponseBody> postUnfollow(@Part("userId") RequestBody userId,
                                   @Header("Authorization") String token);


    //get my follow requests
    @Headers({"Accept: application/json"})
    @POST("/api/get-my-follow-requests")
    Call<ResponseBody> postMyFollowRequests(@Header("Authorization") String token);

    //accept follow request
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/accept-follow-request")
    Call<ResponseBody> postAcceptFollowRequest(@Part("fromUserId") RequestBody fromUserId,
                                               @Header("Authorization") String token);


    //accept follow request
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/reject-follow-request")
    Call<ResponseBody> postRejectFollowRequest(@Part("fromUserId") RequestBody fromUserId,
                                               @Header("Authorization") String token);


    //get followers
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/get-my-followers")
    Call<ResponseBody> postGetFollowers(@Part("userId") RequestBody userId,
                                        @Header("Authorization") String token);

    //get followings
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/get-my-followings")
    Call<ResponseBody> postGetFollowings(@Part("userId") RequestBody userId,
                                        @Header("Authorization") String token);


    //change profile picture method
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/change-profile-image")
    Call<ResponseBody> postChangeProfilePicture(@Part MultipartBody.Part part,
                                                @Header("Authorization") String token);


    //Messaging api's

    //get my messages method
    @Headers({"Accept: application/json"})
    @POST("/api/get-message-user")
    Call<ResponseBody> postGetMyMessages(@Header("Authorization") String token);


    //get messages from my and user
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/get-message")
    Call<ResponseBody> postGetMessageFromUser(@Part("userId") RequestBody userId,
                                              @Part("page") RequestBody page,
                                              @Header("Authorization") String token);


    //send message to user
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/send-message")
    Call<ResponseBody> postSendMessageToUser(@Part("userId") RequestBody userId,
                                             @Part("message") RequestBody message,
                                             @Header("Authorization") String token);




    //check code is verify method
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/check-code-is-correct-for-auth-verify")
    Call<ResponseBody> postCodeVerify(@Part("code") RequestBody code, @Header("Authorization") String token);

    //resend sms method
    @Headers({"Accept: application/json"})
    @POST("/api/resend-sms-for-auth-verify")
    Call<ResponseBody> postResendSms(@Header("Authorization") String token);


    //forgot password phone
    @GET("/api/send-random-password")
    Call<ResponseBody> forgotPasswordWithPhone(@Query("mobile") String mobile);

    //forgot password email
    @GET("/api/send-random-password-to-email")
    Call<ResponseBody> forgotPasswordWithEmail(@Query("email") String email);

    //change password api
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/change-password")
    Call<ResponseBody> postChangePassword(@Part("new") RequestBody newBody,
                                          @Part("old") RequestBody oldBody,
                                          @Header("Authorization") String token);



    //my favorites method
    @Headers({"Accept: application/json"})
    @POST("/api/my-favourite")
    Call<ResponseBody> postMyFavirtes(@Query("page") int page, @Query("languageId") int languageId, @Header("Authorization") String token);

    //add to favorites method
    @Headers({"Accept: application/json"})
    @POST("/api/add-to-favourite")
    Call<ResponseBody> postAddtoFavorites(@Query("book_id") int bookId, @Header("Authorization") String token);

    //remove from favorites method
    @Headers({"Accept: application/json"})
    @POST("/api/remove-from-favourite")
    Call<ResponseBody> postRemoveFromFavorites(@Query("book_id") int bookId, @Header("Authorization") String token);

    //my all books method
    @Headers({"Accept: application/json"})
    @POST("/api/my-all-books")
    Call<ResponseBody> postMyAllBooks(@Query("page") int page, @Query("languageId") int languageId, @Header("Authorization") String token);





    //Stripe payment methods
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/get-ephemeral-key")
    Observable<ResponseBody> getEphermenalKey(@FieldMap Map<String, String> apiVersionMap,
                                              @Header("Authorization") String token);

    //create payment intent for single book
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/intent-book")
    Call<ResponseBody> createPaymentIntentSingleBook(@Part("bookId") RequestBody bookId,
                                           @Header("Authorization") String token);


    //create payment intent for single book
    @Headers({"Accept: application/json"})
    @POST("/api/intent-all-book")
    Call<ResponseBody> createPaymentIntentAllBooks(@Header("Authorization") String token);



    //add single book to my books
    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/api/add-to-my-books")
    Call<ResponseBody> addSingleBookToMyBooksOrGift(@Part("bookId") RequestBody bookId,
                                                    @Part("toUserId") RequestBody userId,
                                                    @Header("Authorization") String token);


    //add all cart books to my books
    @Headers({"Accept: application/json"})
    @POST("/api/add-all-to-my-books")
    Call<ResponseBody> addAllBooksToMyBooks(@Header("Authorization") String token);



}
