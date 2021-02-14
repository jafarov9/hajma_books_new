package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.SeeAllBooksFragment;
import com.hajma.apps.hajmabooks.model.CollectionApiModel;

import java.util.List;

public class CollectionsLargeAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<CollectionApiModel> collectionList;

    public CollectionsLargeAdapter(Context context, List<CollectionApiModel> collectionList) {
        this.collectionList = collectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collections, parent, false);
        return new CollectionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CollectionsHolder collectionsHolder = (CollectionsHolder) holder;

        PicassoCache.getPicassoInstance(context)
                .load(collectionList.get(position).getHorizontal_small()
                        .replace("http:", "https:"))
                .into(collectionsHolder.ivCardCollection);
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    //new books holder
    class CollectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivCardCollection;
        private LinearLayout lnrCollections;


        public CollectionsHolder(@NonNull View itemView) {
            super(itemView);

            ivCardCollection = itemView.findViewById(R.id.imgCollection);
            lnrCollections = itemView.findViewById(R.id.lnrCollections);

            lnrCollections.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            int collectionID = collectionList.get(position).getId();
            String collTitle = collectionList.get(position).getName();

            SeeAllBooksFragment seeAllBooksFragment = new SeeAllBooksFragment(C.TYPE_COLLECTION, null, collectionID, -1, collTitle, null, "", -1);

            ((HomeActivity) context).loadFragment(seeAllBooksFragment, "seeAllColections");


        }
    }
}
