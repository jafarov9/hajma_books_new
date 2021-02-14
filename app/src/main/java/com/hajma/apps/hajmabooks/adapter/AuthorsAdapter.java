package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.SeeAllBooksFragment;
import com.hajma.apps.hajmabooks.model.AuthorApiModel;

import java.util.ArrayList;

public class AuthorsAdapter extends RecyclerView.Adapter<AuthorsAdapter.AuthourViewHolder> {

    private Context context;
    private ArrayList<AuthorApiModel> authorsList;

    public AuthorsAdapter(Context context, ArrayList<AuthorApiModel> authorsList) {
        this.context = context;
        this.authorsList = authorsList;
    }

    @NonNull
    @Override
    public AuthourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_authors_peoples, parent, false);

        return new AuthourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthourViewHolder holder, int position) {
        holder.txtAuthorSearchName.setText(authorsList.get(position).getName());

        PicassoCache.getPicassoInstance(context)
                .load(authorsList.get(position).getProfile().replace("http:", "https:"))
                .into(holder.imgAuthorSearchCover);

    }

    @Override
    public int getItemCount() {
        return authorsList.size();
    }

    //author view holder
    class AuthourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgAuthorSearchCover;
        private TextView txtAuthorSearchName;
        private CardView cardSearchAuthor;



        public AuthourViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAuthorSearchCover = itemView.findViewById(R.id.imgSearhAuthorCover);
            txtAuthorSearchName = itemView.findViewById(R.id.txtSearchAuthorName);
            cardSearchAuthor = itemView.findViewById(R.id.cardSearhAuthor);
            cardSearchAuthor.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            int authorId = authorsList.get(position).getId();

            SeeAllBooksFragment seeByAuthor
                    = new SeeAllBooksFragment(C.TYPE_AUTHOR,
                    null,
                    -1,
                    -1,
                    null,
                    null,
                    "",
                    authorId);

            ((HomeActivity) context).loadFragment(seeByAuthor, "seeByAuthor");
        }
    }
}
