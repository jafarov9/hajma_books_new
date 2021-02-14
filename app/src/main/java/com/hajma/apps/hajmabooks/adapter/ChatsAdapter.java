package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.ChatActivity;
import com.hajma.apps.hajmabooks.model.ChatsApiModel;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>{

    private ArrayList<ChatsApiModel> chatsList;
    private Context context;
    private boolean profilePhotoIsEmpty;


    public ChatsAdapter(ArrayList<ChatsApiModel> chatsList, Context context) {
        this.chatsList = chatsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_messages, parent, false);


        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {

        holder.txtMessageSenderName.setText(chatsList.get(position).getName());
        holder.txtLastMessage.setText(chatsList.get(position).getLast_message());

        String profilePhoto = chatsList.get(position).getProfile();
        profilePhotoIsEmpty = profilePhoto.isEmpty();

        if(!profilePhotoIsEmpty) {
            PicassoCache.getPicassoInstance(context)
                    .load(chatsList.get(position).getProfile().replace("http:", "https:"))
                    .into(holder.imgMessageCover);
        }else {
            holder.imgMessageCover.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

    }

    @Override
    public int getItemCount() {

        Log.e("chatsss", chatsList.size()+"");
        return chatsList.size();
    }

    //chats view holder
    class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgMessageCover;
        private TextView txtMessageSenderName;
        private TextView txtLastMessage;
        private LinearLayout lnrMessages;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMessageCover = itemView.findViewById(R.id.imgMessageCover);
            txtMessageSenderName = itemView.findViewById(R.id.txtMessageSenderName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            lnrMessages = itemView.findViewById(R.id.lnrMessages);
            lnrMessages.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            int senderUserId = chatsList.get(position).getUser_id();
            String name = chatsList.get(position).getName();

            Log.e("klkl", "USERID"+senderUserId);

            Intent intent = new Intent(context, ChatActivity.class);

            intent.putExtra("name", name);
            intent.putExtra("senderID", senderUserId);
            context.startActivity(intent);

        }
    }
}
