package com.hajma.apps.hajmabooks.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.FollowsAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.FollowRequestApiModel;

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

public class FragmentFollows extends Fragment {

    private int type;
    private RecyclerView rvFollows;
    private TextView txtMyFollows;
    private FollowsAdapter followsAdapter;
    private ArrayList<FollowRequestApiModel> followList;
    private ImageButton imgBtnBackMyFollows;
    private int userId;
    private UserDAOInterface userDAOInterface;
    private String token;



    public FragmentFollows(int type, int userId) {
        this.type = type;
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follows, container, false);

        token =
                getActivity()
                        .getSharedPreferences("usercontrol", Context.MODE_PRIVATE)
                .getString("token", null);

        userDAOInterface = ApiUtils.getUserDAOInterface();


        txtMyFollows = view.findViewById(R.id.txtMyFollows);

        if(type == C.TYPE_FOLLOWINGS) {
            txtMyFollows.setText(getResources().getString(R.string._following));
        }else if(type == C.TYPE_FOLLOWERS) {
            txtMyFollows.setText(getResources().getString(R.string._followers));
        }

        //init variables
        imgBtnBackMyFollows = view.findViewById(R.id.imgBtnBackMyFollows);
        imgBtnBackMyFollows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rvFollows = view.findViewById(R.id.rvMyFollows);

        setupRecyclerView();

       return view;
    }

    private void setupRecyclerView() {

        followList = new ArrayList<>();
        followsAdapter = new FollowsAdapter(getActivity(), followList);
        rvFollows.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFollows.setAdapter(followsAdapter);


        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {

        if(type == C.TYPE_FOLLOWERS) {
            getFollowers();
        }

        if (type == C.TYPE_FOLLOWINGS) {
            getFollowings();
        }

    }

    private void getFollowings() {

        RequestBody userIdBody =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        userDAOInterface.postGetFollowings(userIdBody, "Bearer "+ token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {

                    try {
                        String s = response.body().string();
                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        JSONArray data = success.getJSONArray("data");

                        for(int i = 0; i < data.length();i++) {

                            JSONObject j = data.getJSONObject(i);

                            FollowRequestApiModel follow = new FollowRequestApiModel();
                            follow.setUser_id(j.getInt("user_id"));
                            follow.setProfile(j.getString("profile"));
                            follow.setUser_name(j.getString("user_name"));


                            followList.add(follow);
                        }

                        followsAdapter.notifyDataSetChanged();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    return;
                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void getFollowers() {

        RequestBody userIdBody =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        userDAOInterface.postGetFollowers(userIdBody, "Bearer "+ token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {

                    try {
                        String s = response.body().string();
                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        JSONArray data = success.getJSONArray("data");

                        for(int i = 0; i < data.length();i++) {

                            JSONObject j = data.getJSONObject(i);

                            FollowRequestApiModel follow = new FollowRequestApiModel();
                            follow.setUser_id(j.getInt("user_id"));
                            follow.setProfile(j.getString("profile"));
                            follow.setUser_name(j.getString("user_name"));


                            followList.add(follow);
                        }

                        followsAdapter.notifyDataSetChanged();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    return;
                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }
}
