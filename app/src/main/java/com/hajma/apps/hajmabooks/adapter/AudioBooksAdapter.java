package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.DetailedBookFragment;
import com.hajma.apps.hajmabooks.model.BookApiModel;

import java.util.ArrayList;

public class AudioBooksAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<BookApiModel> audioBookList;
    private boolean isFree;

    public AudioBooksAdapter(Context context, ArrayList<BookApiModel> audioBookList) {
        this.context = context;
        this.audioBookList = audioBookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio_books, parent, false);
        return new AudioBookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AudioBookHolder stHolder = (AudioBookHolder) holder;
        stHolder.txtAudioBooksName.setText(audioBookList.get(position).getName());

        String price = audioBookList.get(position).getPrice();

        isFree = price.equals("0.00");
        //Log.e("zxzx", price);

        //Price button color control
        if (!isFree) {
            stHolder.btnPriceAudioBooks.setBackgroundResource(R.drawable.btn_price_background);
            stHolder.btnPriceAudioBooks.setTextColor(Color.parseColor("#2C6DE8"));
            stHolder.btnPriceAudioBooks.setText("$"+audioBookList.get(position).getPrice());
        }else {
            stHolder.btnPriceAudioBooks.setBackgroundResource(R.drawable.btn_free_background);
            stHolder.btnPriceAudioBooks.setTextColor(Color.parseColor("#FFFFFF"));
            stHolder.btnPriceAudioBooks.setText(context.getResources().getString(R.string.free));
        }

        PicassoCache.getPicassoInstance(context)
                .load(audioBookList.get(position).getCover().replace("http:", "https:"))
                .into(stHolder.imgAudioBooks);

    }

    @Override
    public int getItemCount() {
        return audioBookList.size();
    }

    //new books holder
    class AudioBookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgAudioBooks;
        private TextView txtAudioBooksName;
        private Button btnPriceAudioBooks;
        private LinearLayout lnrAudioBooks;

        public AudioBookHolder(@NonNull View itemView) {
            super(itemView);

            imgAudioBooks = itemView.findViewById(R.id.imgAudioBooks);
            txtAudioBooksName = itemView.findViewById(R.id.txtAudioBooksName);
            btnPriceAudioBooks = itemView.findViewById(R.id.btnPriceAudioBooks);
            lnrAudioBooks = itemView.findViewById(R.id.lnrAudioBooks);
            lnrAudioBooks.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int positon = getAdapterPosition();
            int bookID = audioBookList.get(positon).getId();

            DetailedBookFragment dtFragment = new DetailedBookFragment(2, bookID);
            ((HomeActivity) context).loadFragment(dtFragment, "frgdetail");
        }
    }
}
