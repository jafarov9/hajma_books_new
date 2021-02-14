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

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.SeeAllBooksFragment;
import com.hajma.apps.hajmabooks.model.CategoryApiModel;

import java.util.ArrayList;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<CategoryApiModel> categories;
    private int images[] = {
            R.drawable.categoryimage0,
            R.drawable.categoryimage1,
            R.drawable.categoryimage2,
            R.drawable.categoryimage3,
    };
    private int randomImage;


    public CategoryAdapter(Context context, ArrayList<CategoryApiModel> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_category_design, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        holder.txtCategoriesName.setText(categories.get(position).getName());

        int high = 4;
        int low = 0;
        randomImage = new Random().nextInt(high - low) + low;

        holder.imgCategories.setBackgroundResource(images[randomImage]);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    //category view holder
    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgCategories;
        private TextView txtCategoriesName;
        private LinearLayout lnrCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCategories = itemView.findViewById(R.id.imgCategory);
            txtCategoriesName = itemView.findViewById(R.id.txtCategoryName);
            lnrCategory = itemView.findViewById(R.id.lnrCategory);
            lnrCategory.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int catID = categories.get(position).getId();
            String catTitle = categories.get(position).getName();

            SeeAllBooksFragment seeAllBooksFragment
                    = new SeeAllBooksFragment(C.TYPE_CATEGORY, null, -1, catID, null, catTitle, "", -1);

            ((HomeActivity) context).loadFragment(seeAllBooksFragment, "seeAllCategories");
        }
    }
}
