package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.model.MessageApiModel;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<MessageApiModel> messageList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private int fromUserId;

    public MessageListAdapter(Context context, ArrayList<MessageApiModel> messageList, int fromUserId) {
        this.context = context;
        this.messageList = messageList;
        this.fromUserId = fromUserId;


    }



    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        Log.e("klkl", "Oncreate ");


        View view;

        if(viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).messageText.setText(messageList.get(position).getMessage());
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).messageText.setText(messageList.get(position).getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Log.e("aydaa", "Burdayam");


        int id  = messageList.get(position).getFrom_user();

        if(id == fromUserId) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_SENT;
        }


    }


    //Received messages viewholder
    class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        private TextView messageText, timeText, nameText;


        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body_received);
        }


    }

    //Sent messages viewholder
    class SentMessageHolder extends RecyclerView.ViewHolder {

        private TextView messageText, timeText, nameText;


        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
        }


    }

}
