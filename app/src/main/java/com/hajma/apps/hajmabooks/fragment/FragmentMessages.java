package com.hajma.apps.hajmabooks.fragment;

import android.content.Context;
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

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.ChatsAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.ChatsApiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMessages extends Fragment {

    private RecyclerView rvChatsList;
    private ImageButton imgButtonCloseMessages;
    private ArrayList<ChatsApiModel> chatsList;
    private ChatsAdapter chatsAdapter;
    private UserDAOInterface userDIF;
    private String token;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        token = getActivity()
                .getSharedPreferences("usercontrol", Context.MODE_PRIVATE)
                .getString("token", null);

        userDIF = ApiUtils.getUserDAOInterface();

        rvChatsList = view.findViewById(R.id.rvChatsList);
        imgButtonCloseMessages = view.findViewById(R.id.imgButtonCloseMessages);

        imgButtonCloseMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {

        chatsList = new ArrayList<>();

        chatsAdapter = new ChatsAdapter(chatsList, getActivity());
        rvChatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvChatsList.setAdapter(chatsAdapter);

        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {

        chatsList.clear();

        if(token == null) {
            return;
        }

        userDIF.postGetMyMessages("Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                Log.e("mmmmm", "Onresponse");

                if(response.isSuccessful()) {
                    try {

                        Log.e("mmmmm", "success");


                        String s = response.body().string();
                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        JSONArray messages = success.getJSONArray("messages");

                        for(int i = 0; i < messages.length();i++) {


                            JSONObject temp = messages.getJSONObject(i);

                            ChatsApiModel chats = new ChatsApiModel();
                            chats.setEmail(temp.getString("email"));
                            chats.setLast_message(temp.getString("last_message"));
                            chats.setName(temp.getString("name"));
                            chats.setUser_id(temp.getInt("user_id"));
                            chats.setMessage_count(temp.getInt("message_count"));
                            chats.setProfile(temp.getString("profile"));
                            chats.setUsername(temp.getString("username"));
                            chats.setLast_message_date(temp.getString("last_message_date"));
                            chats.setLast_message_id(temp.getInt("last_message_id"));


                            chatsList.add(chats);
                        }

                        chatsAdapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {

                    Log.e("mmmmm", "error");

                    return;
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("mmmmm", "failed");

            }
        });

    }
}
