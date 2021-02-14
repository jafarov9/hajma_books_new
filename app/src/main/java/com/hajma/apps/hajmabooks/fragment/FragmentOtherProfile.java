package com.hajma.apps.hajmabooks.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.ChatActivity;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.adapter.SeeAllBooksAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.model.UserApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentOtherProfile extends Fragment {



    private int userID;
    private Button btnFollowToOtherProfile;
    private Button btnMessageToOtherProfile;

    private ImageButton imageButtonBackFromOtherProfile;
    private ImageView imgOtherProfileCover;
    private TextView txtOtherProfileName;
    private TextView txtOtherProfileBookCount, txtOtherProfileFollowingCount,
        txtOtherProfileFollowersCount, txtOtherProfilePresentsCount;
    private UserDAOInterface userDIF;
    private String token;
    private SharedPreferences sharedPreferences;
    private UserApiModel otherUser;
    private int langID;
    private ArrayList<BookApiModel> bookList;
    private RecyclerView rvOtherProfileBooks;
    private SeeAllBooksAdapter otherBooksAdapter;



    public FragmentOtherProfile(int userID) {
        this.userID = userID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_other, container, false);

        String language = LocaleHelper.getPersistedData(getActivity(), "az");

        if(language.equals("az")) {
            langID = C.LANGUAGE_AZ;
        } else if(language.equals("en")) {
            langID = C.LANGUAGE_EN;
        }else if(language.equals("ru")) {
            langID = C.LANGUAGE_RU;
        }else {
            langID = C.LANGUAGE_AZ;
        }


        userDIF = ApiUtils.getUserDAOInterface();
        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);

        token = sharedPreferences.getString("token", null);

        //initialize variables
        btnFollowToOtherProfile = view.findViewById(R.id.btnFollowToOtherProfile);
        btnMessageToOtherProfile = view.findViewById(R.id.btnMessageToOtherProfile);

        rvOtherProfileBooks = view.findViewById(R.id.rvOtherProfileBooks);
        setupRecyclerView();

        imgOtherProfileCover = view.findViewById(R.id.imgOtherProfileCover);
        txtOtherProfileName = view.findViewById(R.id.txtOtherProfileName);
        txtOtherProfileBookCount = view.findViewById(R.id.txtOtherProfileBooksCount);
        txtOtherProfilePresentsCount = view.findViewById(R.id.txtOtherProfilePresentsCount);
        txtOtherProfileFollowersCount = view.findViewById(R.id.txtOtherrofileFollowersCount);
        txtOtherProfileFollowingCount = view.findViewById(R.id.txtOtherProfileFollowingCount);
        imageButtonBackFromOtherProfile = view.findViewById(R.id.imageButtonBackFromOtherProfile);
        imageButtonBackFromOtherProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if(token != null) {
            loadOtherProfileDetails(langID, userID);
        }

        txtOtherProfileFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otherUser.getFollowing().equals("following")) {
                    FragmentFollows frgFollows = new FragmentFollows(C.TYPE_FOLLOWERS, userID);
                    ((HomeActivity)getActivity()).loadFragment(frgFollows, "frgFollows");
                }
            }
        });

        txtOtherProfileFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otherUser.getFollowing().equals("following")) {
                    FragmentFollows frgFollows = new FragmentFollows(C.TYPE_FOLLOWINGS, userID);
                    ((HomeActivity)getActivity()).loadFragment(frgFollows, "frgFollows");
                }
            }
        });

        btnFollowToOtherProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check follow type
                if(otherUser.getFollowing().equals("not-following")) {
                    sendFollowRequest(userID, token);
                }else if(otherUser.getFollowing().equals("following")) {
                    unfollowRequest(userID, token);
                }

            }
        });

        btnMessageToOtherProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("name", otherUser.getName());
                intent.putExtra("senderID", userID);
                startActivity(intent);
            }
        });



        return view;
    }

    private void setupRecyclerView() {
        bookList = new ArrayList<>();
        otherBooksAdapter = new SeeAllBooksAdapter(getActivity(), bookList, C.OTHER_BOOK);
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rvOtherProfileBooks.setLayoutManager(layoutManager);
        rvOtherProfileBooks.setAdapter(otherBooksAdapter);

    }

    //send follow request
    private void sendFollowRequest(int userId, String token) {

        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userID));

        userDIF.postSendFollowRequest(userIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    loadOtherProfileDetails(1, userId);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //unfollow request
    private void unfollowRequest(int userID, String token) {

        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userID));

        userDIF.postUnfollow(userIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    loadOtherProfileDetails(1, userID);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void loadOtherProfileDetails(int langID, int userId) {

        RequestBody langBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(langID));
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        userDIF.postOtherProfile(langBody, userIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    try {
                        otherUser = new UserApiModel();

                        String s = response.body().string();

                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        JSONObject book = success.getJSONObject("book");

                        otherUser.setName(success.getString("name"));
                        otherUser.setUsername(success.getString("username"));
                        otherUser.setProfile(success.getString("profile"));
                        otherUser.setVerified(success.getBoolean("verified"));
                        otherUser.setMobile(success.getString("mobile"));
                        otherUser.setEmail(success.getString("email"));
                        otherUser.setFollowing(success.getString("following"));
                        otherUser.setFollow_request_count(success.getInt("follow_request_count"));
                        otherUser.setFollower_count(success.getInt("follewer_count"));
                        otherUser.setFollowing_count(success.getInt("following_count"));
                        otherUser.setPresent_count(success.getInt("present_count"));
                        otherUser.setBookCount(book.getInt("count"));
                        loadViewsWithData();

                        if(otherUser.getFollowing().equals("following")) {

                            JSONArray data = book.getJSONArray("data");

                            for(int i = 0; i < data.length();i++) {
                                JSONObject j = data.getJSONObject(i);
                                BookApiModel tBook = new BookApiModel();
                                tBook.setId(j.getInt("id"));
                                tBook.setName(j.getString("name"));
                                tBook.setPage_count(j.getInt("page_count"));
                                tBook.setYear(j.getInt("year"));
                                tBook.setCover(j.getString("cover"));
                                tBook.setPrice(j.getString("price"));
                                tBook.setSound_count(j.getInt("sound_count"));

                                bookList.add(tBook);

                            }

                            otherBooksAdapter.notifyDataSetChanged();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void loadViewsWithData() {

        txtOtherProfileName.setText(otherUser.getName());
        txtOtherProfilePresentsCount.setText(""+otherUser.getPresent_count());
        txtOtherProfileBookCount.setText(""+otherUser.getBookCount());
        txtOtherProfileFollowersCount.setText(""+otherUser.getFollower_count());
        txtOtherProfileFollowingCount.setText(""+ otherUser.getFollowing_count());

        String followtype = otherUser.getFollowing();

        if(followtype.equals("not-following")) {
            btnFollowToOtherProfile.setText(getResources().getString(R.string._follow));
        }else if(followtype.equals("requested")) {
            btnFollowToOtherProfile.setText(getResources().getString(R.string._requested));
        } else if(followtype.equals("following")) {
            btnFollowToOtherProfile.setText(getResources().getString(R.string._following));
        }

        if(!otherUser.getProfile().equals("")) {
            Picasso.get()
                    .load(otherUser.getProfile()
                            .replace("http:", "https:"))
                    .into(imgOtherProfileCover);
        }


    }
}
