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
import com.hajma.apps.hajmabooks.model.CategoryApiModel;

import java.util.List;

public class CategoryLargeAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<CategoryApiModel> categoryList;

    public CategoryLargeAdapter(Context context, List<CategoryApiModel> categoryList) {
        this.categoryList = categoryList;
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
                .load(categoryList.get(position).getHorizontal_small()
                        .replace("http:", "https:"))
                .into(collectionsHolder.ivCardCollection);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    //new books holder
    class CollectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivCardCollection;
        private LinearLayout lnrCategory;

        public CollectionsHolder(@NonNull View itemView) {
            super(itemView);
            ivCardCollection = itemView.findViewById(R.id.imgCollection);
            lnrCategory = itemView.findViewById(R.id.lnrCollections);
            lnrCategory.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            int catID = categoryList.get(position).getId();
            String catTitle = categoryList.get(position).getName();

            SeeAllBooksFragment seeAllBooksFragment
                    = new SeeAllBooksFragment(C.TYPE_CATEGORY, null, -1, catID, null, catTitle, "", -1);

            ((HomeActivity) context).loadFragment(seeAllBooksFragment, "seeAllCategories");

        }
    }
}
