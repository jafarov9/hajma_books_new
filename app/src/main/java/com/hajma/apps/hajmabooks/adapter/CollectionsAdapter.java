package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.SeeAllBooksFragment;
import com.hajma.apps.hajmabooks.model.CollectionApiModel;

import java.util.ArrayList;
import java.util.Random;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionViewHolder> {

    private Context context;
    private ArrayList<CollectionApiModel> collections;
    private int randomImage;
    private int images[] = {
            R.drawable.categoryimage0,
            R.drawable.categoryimage1,
            R.drawable.categoryimage2,
            R.drawable.categoryimage3,
    };

    public CollectionsAdapter(Context context, ArrayList<CollectionApiModel> collections) {
        this.context = context;
        this.collections = collections;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_category_design, parent, false);
        return new CollectionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {

        holder.txtCollectionsName.setText(collections.get(position).getName());

        int high = 4;
        int low = 0;
        randomImage = new Random().nextInt(high - low) + low;

        holder.imgCollections.setBackgroundResource(images[randomImage]);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    //category view holder
    class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgCollections;
        private TextView txtCollectionsName;
        private CardView cardCollections;
        private LinearLayout lnrCollections;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCollections = itemView.findViewById(R.id.imgCategory);
            txtCollectionsName = itemView.findViewById(R.id.txtCategoryName);
            cardCollections = itemView.findViewById(R.id.cardViewCategory);
            lnrCollections = itemView.findViewById(R.id.lnrCategory);
            lnrCollections.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            int collectionID = collections.get(position).getId();
            String collTitle = collections.get(position).getName();

            SeeAllBooksFragment seeAllBooksFragment = new SeeAllBooksFragment(C.TYPE_COLLECTION, null, collectionID, -1, collTitle, null, "", -1);

            ((HomeActivity) context).loadFragment(seeAllBooksFragment, "seeAllColections");


        }
    }
}
