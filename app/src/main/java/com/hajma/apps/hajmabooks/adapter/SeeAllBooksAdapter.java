package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.DetailedBookFragment;
import com.hajma.apps.hajmabooks.fragment.SeeAllBooksFragment;
import com.hajma.apps.hajmabooks.model.BookApiModel;

import java.util.List;

public class SeeAllBooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BookApiModel> bookList;
    private Context context;
    private boolean isFree;
    private int type;


    public SeeAllBooksAdapter(Context context, List<BookApiModel> bookList, int type) {
        this.context = context;
        this.bookList = bookList;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_see_all_books, parent, false);

        return new SeeAllViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SeeAllViewHolder mHolder = (SeeAllViewHolder) holder;
        mHolder.txtSeeAllBooksName.setText(bookList.get(position).getName());
        String price = bookList.get(position).getPrice();

        isFree = price.equals("0.00");
        //Log.e("zxzx", price);

        //Price button color control

        if(type == C.MY_BOOK) {

            if(bookList.get(position).getSound_count() > 0) {
                mHolder.btnSeeAllPrice.setBackgroundResource(R.drawable.btn_free_background);
                mHolder.btnSeeAllPrice.setTextColor(Color.parseColor("#FFFFFF"));
                mHolder.btnSeeAllPrice.setText(context.getResources().getString(R.string._listen));
            }else {
                mHolder.btnSeeAllPrice.setBackgroundResource(R.drawable.btn_read_background);
                mHolder.btnSeeAllPrice.setTextColor(Color.parseColor("#FFFFFF"));
                mHolder.btnSeeAllPrice.setText(context.getResources().getString(R.string._read));
            }
        }else if (!isFree) {
            mHolder.btnSeeAllPrice.setBackgroundResource(R.drawable.btn_price_background);
            mHolder.btnSeeAllPrice.setTextColor(Color.parseColor("#2C6DE8"));
            mHolder.btnSeeAllPrice.setText("$"+bookList.get(position).getPrice());
        }else {
            mHolder.btnSeeAllPrice.setBackgroundResource(R.drawable.btn_free_background);
            mHolder.btnSeeAllPrice.setTextColor(Color.parseColor("#FFFFFF"));
            mHolder.btnSeeAllPrice.setText(context.getResources().getString(R.string.free));
        }

        PicassoCache.getPicassoInstance(context)
                .load(bookList.get(position).getCover().replace("http:", "https:"))
                .into(mHolder.imgSeeAllBooks);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    //see all view holder
    class SeeAllViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgSeeAllBooks;
        private TextView txtSeeAllBooksName;
        private Button btnSeeAllPrice;
        private CardView cardSeeAll;

        public SeeAllViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSeeAllBooks = itemView.findViewById(R.id.imgSeeAllBooks);
            txtSeeAllBooksName =  itemView.findViewById(R.id.txtSeeAllBooksName);
            btnSeeAllPrice = itemView.findViewById(R.id.btnPriceSeeAllBooks);
            cardSeeAll = itemView.findViewById(R.id.cardSeeAll);
            cardSeeAll.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            int bookID = bookList.get(position).getId();

            DetailedBookFragment detailedBookFragment = new DetailedBookFragment(1, bookID);

            ((HomeActivity) context).loadFragment(detailedBookFragment, "dtSeeAll");

        }
    }
}
