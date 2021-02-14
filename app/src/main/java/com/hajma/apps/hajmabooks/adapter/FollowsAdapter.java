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

import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.FragmentOtherProfile;
import com.hajma.apps.hajmabooks.model.FollowRequestApiModel;

import java.util.ArrayList;

public class FollowsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<FollowRequestApiModel> followList;
    private boolean profilePhotoIsEmpty;


    public FollowsAdapter(Context context, ArrayList<FollowRequestApiModel> followList) {
        this.context = context;
        this.followList = followList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_follows, parent, false);


        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FollowViewHolder followViewHolder = (FollowViewHolder) holder;

        followViewHolder.txtFollowsName.setText(followList.get(position).getUser_name());

        String profilePhoto = followList.get(position).getProfile();
        profilePhotoIsEmpty = profilePhoto.isEmpty();

        if(!profilePhotoIsEmpty) {
            PicassoCache.getPicassoInstance(context)
                    .load(followList.get(position).getProfile().replace("http:", "https:"))
                    .into(followViewHolder.imgFollows);
        }else {
            followViewHolder.imgFollows.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    //follow view holder
    class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgFollows;
        private TextView txtFollowsName;
        private LinearLayout lnrFollows;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFollows = itemView.findViewById(R.id.imgFollows);
            txtFollowsName = itemView.findViewById(R.id.txtFollowsName);
            lnrFollows = itemView.findViewById(R.id.lnrFollowss);
            lnrFollows.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            int userId = followList.get(position).getUser_id();

            FragmentOtherProfile frgOtherProfile = new FragmentOtherProfile(userId);

            ((HomeActivity)context).loadFragment(frgOtherProfile, "frgOtherProfile");
        }
    }
}
