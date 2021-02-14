package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.model.FollowRequestApiModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FollowRequestsAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<FollowRequestApiModel> requestList;
    private boolean profilePhotoIsEmpty;

    public FollowRequestsAdapter(Context context, ArrayList<FollowRequestApiModel> requestList) {
        this.requestList = requestList;
        this.context = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_follow_requests, parent, false);
        return new FollowRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FollowRequestViewHolder fHolder =(FollowRequestViewHolder) holder;

        fHolder.txtFollowRequestName.setText(requestList.get(position).getUser_name());

        String profilePhoto = requestList.get(position).getProfile();
        profilePhotoIsEmpty = profilePhoto.isEmpty();

        if(!profilePhotoIsEmpty) {
            PicassoCache.getPicassoInstance(context)
                    .load(requestList.get(position).getProfile().replace("http:", "https:"))
                    .into(fHolder.imgFollowRequestCover);
        }else {
            fHolder.imgFollowRequestCover.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class FollowRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgFollowRequestCover;
        private TextView txtFollowRequestName;
        private TextView txtAcceptFollow;
        private TextView txtRejectFollow;
        private LinearLayout lnrFollowRequests;



        public FollowRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFollowRequestCover = itemView.findViewById(R.id.imgFollowRequestCover);
            txtFollowRequestName = itemView.findViewById(R.id.txtFollowRequestName);
            txtAcceptFollow = itemView.findViewById(R.id.txtAcceptFollow);
            txtRejectFollow = itemView.findViewById(R.id.txtRejectFollow);
            lnrFollowRequests = itemView.findViewById(R.id.lnrFollowRequests);

            txtAcceptFollow.setOnClickListener(this);
            txtRejectFollow.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.txtAcceptFollow :

                    int positon = getAdapterPosition();
                    int fromUserId = requestList.get(positon).getUser_id();

                    EventBus.getDefault().post(new DataEvent.CallAcceptRejectFollow(1, fromUserId));
                    break;

                case R.id.txtRejectFollow :
                    break;

                case R.id.lnrFollowRequests :
                    break;

                    default: return;
            }

        }
    }
}
