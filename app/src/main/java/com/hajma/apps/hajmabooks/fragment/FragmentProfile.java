package com.hajma.apps.hajmabooks.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.adapter.SeeAllBooksAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.model.UserApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
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

public class FragmentProfile extends Fragment {

    private ImageView imgProfileCover;
    private TextView txtProfileName;
    private TextView txtProfileBiography;
    private ImageButton imageButtonProfileSettings;
    private ImageButton imageButtonProfileAddFriend;
    private ImageButton imageButtonProfileMessage;
    private TextView txtProfileBooksCount, txtProfileFollowingCount,
    txtProfileFollowersCount, txtProfilePresentsCount;
    private RecyclerView rv_profile_books;
    private ArrayList<BookApiModel> myBookList;
    private SeeAllBooksAdapter myBooksAdapter;
    private UserApiModel user;
    private UserDAOInterface userDIF;
    private String token;
    private SharedPreferences sharedPreferences;
    private int langID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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


        //Initialize variables
        imgProfileCover = view.findViewById(R.id.imgProfileCover);
        txtProfileName = view.findViewById(R.id.txtProfileName);
        txtProfileBiography = view.findViewById(R.id.txtProfileBiography);
        txtProfileBooksCount = view.findViewById(R.id.txtProfileBooksCount);
        txtProfileFollowingCount = view.findViewById(R.id.txtProfileFollowingCount);
        txtProfileFollowersCount = view.findViewById(R.id.txtProfileFollowersCount);
        txtProfilePresentsCount = view.findViewById(R.id.txtProfilePresentsCount);
        rv_profile_books = view.findViewById(R.id.rv_profile_books);
        imageButtonProfileSettings = view.findViewById(R.id.imageButtonProfileSettings);
        imageButtonProfileMessage = view.findViewById(R.id.imageButtonProfileMessage);


        txtProfileFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentFollows fragmentFollows = new FragmentFollows(C.TYPE_FOLLOWERS, 0);
                ((HomeActivity)getActivity()).loadFragment(fragmentFollows, "followsFrg");
            }
        });

        txtProfileFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentFollows fragmentFollows = new FragmentFollows(C.TYPE_FOLLOWINGS, 0);
                ((HomeActivity)getActivity()).loadFragment(fragmentFollows, "followsFrg");
            }
        });


        imageButtonProfileMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMessages fragmentMessages = new FragmentMessages();
                ((HomeActivity) getActivity()).loadFragment(fragmentMessages, "messagesFrg");
            }
        });

        imageButtonProfileAddFriend = view.findViewById(R.id.imageButtonAddFriend);
        imageButtonProfileAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FollowRequestsFragment followRequestsFragment = new FollowRequestsFragment();
                ((HomeActivity) getActivity()).loadFragment(followRequestsFragment, "frequests");

            }
        });

        imageButtonProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileSettingsFragment profileSettingsFragment = new ProfileSettingsFragment(user);
                ((HomeActivity) getActivity()).loadFragment(profileSettingsFragment, "prosettingfrg");
            }
        });
        //set disabled when is data loading
        imageButtonProfileSettings.setEnabled(false);


        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);

        token = sharedPreferences.getString("token", null);

        setupRecyclerView();


        if(token != null) {
            loadUserDetails(langID);
        }

        return view;
    }

    private void setupRecyclerView() {

        myBookList = new ArrayList<>();
        myBooksAdapter = new SeeAllBooksAdapter(getActivity(), myBookList, C.MY_BOOK);
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rv_profile_books.setLayoutManager(layoutManager);
        rv_profile_books.setAdapter(myBooksAdapter);
    }

    private void loadUserDetails(int langID) {

        user =  new UserApiModel();
        myBookList.clear();

        RequestBody langBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(langID));



        userDIF.postMyProfile(langBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {

                    imageButtonProfileSettings.setEnabled(true);

                    try {

                        Log.e("ahaa", "Response Success");
                        String s = response.body().string();
                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        user.setName(success.getString("name"));
                        user.setUsername(success.getString("username"));
                        user.setProfile(success.getString("profile"));
                        user.setVerified(success.getBoolean("verified"));
                        user.setMobile(success.getString("mobile"));
                        user.setEmail(success.getString("email"));
                        user.setFollowing(success.getString("following"));
                        user.setFollow_request_count(success.getInt("follow_request_count"));
                        user.setFollower_count(success.getInt("follewer_count"));
                        user.setFollowing_count(success.getInt("following_count"));
                        user.setPresent_count(success.getInt("present_count"));

                        JSONObject book = success.getJSONObject("book");
                        user.setBookCount(book.getInt("count"));

                        JSONArray data = book.getJSONArray("data");

                        //set my books list with data
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

                            //add book to arraylist
                            myBookList.add(tBook);
                        }

                        loadViewsWithData();
                        myBooksAdapter.notifyDataSetChanged();

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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    private void loadViewsWithData() {

        txtProfileName.setText(user.getName());
        txtProfilePresentsCount.setText(""+user.getPresent_count());
        txtProfileBooksCount.setText(""+myBookList.size());
        txtProfileFollowersCount.setText(""+user.getFollower_count());
        txtProfileFollowingCount.setText(""+ user.getFollowing_count());

        if(!user.getProfile().equals("")) {
            Picasso.get()
                    .load(user.getProfile()
                            .replace("http:", "https:"))
                    .into(imgProfileCover);
        }

    }

    @Subscribe(sticky = true)
    public void OnCallProfileDetailsUpdate(DataEvent.CallProfileDetailsUpdate event) {
        if(event.getResult() == 1) {
            loadUserDetails(langID);
        }
    }

}
