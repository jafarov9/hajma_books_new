package com.hajma.apps.hajmabooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.MessageListAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.MessageApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView reyclerview_message_list;
    private EditText edittext_chatbox;
    private Button button_chatbox_send;
    private UserDAOInterface userDIF;
    private int senderUserId;
    private String token;
    private SharedPreferences sharedPreferences;
    private ArrayList<MessageApiModel> messageList;
    private MessageListAdapter adapter;
    private int pagenumber;
    private TextView txtChatUsername;
    private String chatName;
    private ImageButton imgBtnCloseChat;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init variables
        userDIF = ApiUtils.getUserDAOInterface();
        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        senderUserId = getIntent().getIntExtra("senderID", -1);
        chatName = getIntent().getStringExtra("name");

        txtChatUsername = findViewById(R.id.txtChatUsername);
        txtChatUsername.setText(chatName);

        reyclerview_message_list = findViewById(R.id.reyclerview_message_list);
        button_chatbox_send = findViewById(R.id.button_chatbox_send);

        imgBtnCloseChat = findViewById(R.id.imgBtnCloseChat);
        imgBtnCloseChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        edittext_chatbox = findViewById(R.id.edittext_chatbox);

        setupRecyclerView();


    }

    private Runnable chatUpdater = new Runnable() {
        @Override
        public void run() {
            loadRecyclerViewData(pagenumber);
        }
    };

    private void setupRecyclerView() {

        messageList = new ArrayList<>();
        reyclerview_message_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        Log.e("hjj", "Sender userID : "+senderUserId);

        adapter = new MessageListAdapter(this, messageList, senderUserId);
        reyclerview_message_list.setAdapter(adapter);

        pagenumber = 1;


        loadRecyclerViewData(pagenumber);
    }

    private void loadRecyclerViewData(int page) {

        messageList.clear();
        adapter.notifyDataSetChanged();

        RequestBody userIdBody =
                RequestBody
                        .create(MediaType.parse("text/plain"), String.valueOf(senderUserId));

        RequestBody pagebody =
                RequestBody
                        .create(MediaType.parse("text/plain"), String.valueOf(page));



        userDIF.postGetMessageFromUser(userIdBody, pagebody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {

                    try {

                        Log.e("klkl", "Success");

                        String s = response.body().string();

                        Log.e("klkl", s);

                        JSONObject success = new JSONObject(s).getJSONObject("success");

                        JSONArray messages = success.getJSONArray("messages");

                        for(int i = messages.length() - 1;i >= 0;i--) {

                            JSONObject j = messages.getJSONObject(i);

                            MessageApiModel message = new MessageApiModel();
                            message.setFrom_user(j.getInt("from_user"));
                            message.setDate(j.getString("date"));
                            message.setTo_user(j.getInt("to_user"));
                            message.setMessage(j.getString("message"));

                            messageList.add(message);
                        }


                        adapter.notifyDataSetChanged();
                        reyclerview_message_list.scrollToPosition(adapter.getItemCount() - 1);

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

        handler.postDelayed(chatUpdater, 10000);

    }


    private void sendMessage() {

        if(edittext_chatbox.getText().toString().trim().isEmpty()) {
            return;
        }

        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(senderUserId));
        RequestBody messageBody = RequestBody.create(MediaType.parse("text/plain"),
                edittext_chatbox.getText().toString());

        userDIF.postSendMessageToUser(userIdBody, messageBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    loadRecyclerViewData(pagenumber);
                    edittext_chatbox.setText("");
                }else {
                    Toast.makeText(ChatActivity.this
                            , "Message cannot send"
                            , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivity.this
                        , getResources()
                                .getString(R.string.check_your_internet_connection)
                        , Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(chatUpdater);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
