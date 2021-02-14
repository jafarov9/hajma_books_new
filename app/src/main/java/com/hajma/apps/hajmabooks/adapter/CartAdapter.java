package com.hajma.apps.hajmabooks.adapter;

import android.content.Context;
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
import com.hajma.apps.hajmabooks.model.CartApiModel;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private ArrayList<BookApiModel> cartList;

    public CartAdapter(Context context, ArrayList<BookApiModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_books, parent, false);

        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        PicassoCache.getPicassoInstance(context)
                .load(cartList.get(position).getCover().replace("http:", "https:"))
                .into(holder.imgShoppingCart);

        holder.txtShoppingCartBookName.setText(cartList.get(position).getName());
        holder.txtAuthorNameShoppingCart.setText(cartList.get(position).getName());
        holder.btnPriceShoppingCartBook.setText(cartList.get(position).getPrice());



    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    //Cart view holder
    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgShoppingCart;
        private TextView txtAuthorNameShoppingCart;
        private TextView txtShoppingCartBookName;
        private Button btnPriceShoppingCartBook;
        private LinearLayout lnrCart;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            imgShoppingCart = itemView.findViewById(R.id.imgShoppingCart);
            txtAuthorNameShoppingCart = itemView.findViewById(R.id.txtAuthorNameShoppingCart);
            txtShoppingCartBookName = itemView.findViewById(R.id.txtShoppingCartBookName);
            btnPriceShoppingCartBook = itemView.findViewById(R.id.btnPriceShoppingCartBook);
            lnrCart = itemView.findViewById(R.id.lnrCart);
            lnrCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int positon = getAdapterPosition();
            int bookID = cartList.get(positon).getId();

            DetailedBookFragment dtFragment = new DetailedBookFragment(2, bookID);
            ((HomeActivity) context).loadFragment(dtFragment, "frgdetail");
        }
    }
}
