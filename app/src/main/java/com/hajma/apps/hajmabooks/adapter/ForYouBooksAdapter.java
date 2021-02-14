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

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.PicassoCache;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.fragment.DetailedBookFragment;
import com.hajma.apps.hajmabooks.model.BookApiModel;

import java.util.List;

public class ForYouBooksAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<BookApiModel> bookList;
    private boolean isFree;
    private boolean isSub;


    public ForYouBooksAdapter(Context context, List<BookApiModel> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_standard_books, parent, false);
        return new StandardBookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        StandardBookHolder stHolder = (StandardBookHolder) holder;
        stHolder.tvCardStandardName.setText(bookList.get(position).getName());
        String price = bookList.get(position).getPrice();

        isFree = price.equals("0.00");
        //Log.e("zxzx", price);

        //Price button color control
        if (!isFree) {
            stHolder.btnCardStandardPrice.setBackgroundResource(R.drawable.btn_price_background);
            stHolder.btnCardStandardPrice.setTextColor(Color.parseColor("#2C6DE8"));
            stHolder.btnCardStandardPrice.setText("$"+bookList.get(position).getPrice());
        }else {
            stHolder.btnCardStandardPrice.setBackgroundResource(R.drawable.btn_free_background);
            stHolder.btnCardStandardPrice.setTextColor(Color.parseColor("#FFFFFF"));
            stHolder.btnCardStandardPrice.setText(context.getResources().getString(R.string.free));
        }



        PicassoCache.getPicassoInstance(context)
                .load(bookList.get(position).getCover().replace("http:", "https:"))
                .into(stHolder.ivCardStandard);

    }

    @Override
    public int getItemCount() {
        if(bookList.size() > 0) {
            return isSub ? 10 : bookList.size();
        }else return bookList.size();
    }

    //new books holder
    class StandardBookHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivCardStandard;
        private TextView tvCardStandardName;
        private Button btnCardStandardPrice;
        private LinearLayout lnrForYouBooks;

        public StandardBookHolder(@NonNull View itemView) {
            super(itemView);

            ivCardStandard = itemView.findViewById(R.id.imgStandardBooks);
            tvCardStandardName = itemView.findViewById(R.id.txtCardStandardName);
            btnCardStandardPrice = itemView.findViewById(R.id.btnPrice);
            lnrForYouBooks = itemView.findViewById(R.id.lnrNewBooks);

            lnrForYouBooks.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int positon = getAdapterPosition();
            int bookID = bookList.get(positon).getId();

            DetailedBookFragment dtFragment = new DetailedBookFragment(2, bookID);
            ((HomeActivity) context).loadFragment(dtFragment, "frgdetail");
        }
    }

    public boolean isSub() {
        return isSub;
    }

    public void setSub(boolean sub) {
        isSub = sub;
    }
}
