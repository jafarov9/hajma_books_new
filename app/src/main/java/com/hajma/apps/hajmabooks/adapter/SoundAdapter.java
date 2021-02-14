package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.AudioPlayerActivity;
import com.hajma.apps.hajmabooks.model.SoundApiModel;

import java.util.ArrayList;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder>{

    private ArrayList<SoundApiModel> soundList;
    private Context context;

    public SoundAdapter(ArrayList<SoundApiModel> soundList, Context context) {
        this.soundList = soundList;
        this.context = context;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_sound, parent, false);


        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        holder.txtSoundName.setText(soundList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    //sound view holder
    class SoundViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtSoundName;
        private CardView cardSound;


        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSoundName = itemView.findViewById(R.id.txtSoundName);
            cardSound = itemView.findViewById(R.id.cardSound);
            cardSound.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String url = soundList.get(position).getSound();

            ((AudioPlayerActivity) context).prepareMediaPlayer(url);
        }
    }
}
