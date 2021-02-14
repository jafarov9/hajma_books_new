package com.hajma.apps.hajmabooks.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.FollowRequestsAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.FollowRequestApiModel;

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

public class FollowRequestsFragment extends Fragment {

    private RecyclerView rvFollowRequsts;
    private ImageButton imageButtonBackFromFollowRequsts;
    private UserDAOInterface userDIF;
    private String token;
    private SharedPreferences sharedPreferences;
    private FollowRequestsAdapter requestsAdapter;
    private ArrayList<FollowRequestApiModel> requestList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follow_requests, container, false);

        userDIF = ApiUtils.getUserDAOInterface();
        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);


        //init variables
        imageButtonBackFromFollowRequsts = view.findViewById(R.id.imageButtonBackFollowRequests);
        imageButtonBackFromFollowRequsts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rvFollowRequsts = view.findViewById(R.id.rv_follow_requests);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {

        requestList = new ArrayList<>();
        requestsAdapter = new FollowRequestsAdapter(getActivity(), requestList);
        rvFollowRequsts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFollowRequsts.setAdapter(requestsAdapter);

        //load recyclerview data
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {
        Log.e("zaaaa", "Burdayam");
        userDIF.postMyFollowRequests("Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if(response.isSuccessful()) {
                    try {
                        String s = response.body().string();
                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        JSONArray requests = success.getJSONArray("requests");

                        requestList.clear();

                        //add request to request list
                        for(int i = 0; i < requests.length();i++) {

                            JSONObject j = requests.getJSONObject(i);

                            FollowRequestApiModel request = new FollowRequestApiModel();
                            request.setUser_id(j.getInt("user_id"));
                            request.setUser_name(j.getString("user_name"));
                            request.setProfile(j.getString("profile"));
                            requestList.add(request);
                        }

                        requestsAdapter.notifyDataSetChanged();

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

    @Subscribe
    public void OnCallAcceptRejectFollow(DataEvent.CallAcceptRejectFollow event) {

        Log.e("asdf", "Burdayam");
        if(event.getType() == 1) {
            acceptFollowRequest(event.getUserID());
        }else if(event.getType() == 0) {
            rejectFollowRequest(event.getUserID());
        }
    }

    private void rejectFollowRequest(int fromUserID) {

        RequestBody fromUserIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(fromUserID));

        userDIF.postRejectFollowRequest(fromUserIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    loadRecyclerViewData();
                    EventBus.getDefault().postSticky(new DataEvent.CallProfileDetailsUpdate(1));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void acceptFollowRequest(int fromUserID) {

        RequestBody fromUserIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(fromUserID));

        userDIF.postAcceptFollowRequest(fromUserIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    loadRecyclerViewData();
                    EventBus.getDefault().postSticky(new DataEvent.CallProfileDetailsUpdate(1));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


}
