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
import com.hajma.apps.hajmabooks.fragment.FragmentOtherProfile;
import com.hajma.apps.hajmabooks.fragment.PaymentFragment;
import com.hajma.apps.hajmabooks.model.DetailedBookApiModel;
import com.hajma.apps.hajmabooks.model.PeopleApiModel;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private Context context;
    private ArrayList<PeopleApiModel> peopleList;
    private boolean profilePhotoIsEmpty;
    private int type;
    private DetailedBookApiModel book;

    public PeopleAdapter(Context context, ArrayList<PeopleApiModel> peopleList, int type, DetailedBookApiModel book) {
        this.context = context;
        this.peopleList = peopleList;
        this.book = book;
        this.type = type;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_authors_peoples, parent, false);

        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position) {
        holder.txtPeopleSearchName.setText(peopleList.get(position).getName());

        String profilePhoto = peopleList.get(position).getProfile();
        profilePhotoIsEmpty = profilePhoto.isEmpty();

        if(!profilePhotoIsEmpty) {
            PicassoCache.getPicassoInstance(context)
                    .load(peopleList.get(position).getProfile().replace("http:", "https:"))
                    .into(holder.imgPeopleSearchCover);
        }else {
            holder.imgPeopleSearchCover.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    //author view holder
    class PeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgPeopleSearchCover;
        private TextView txtPeopleSearchName;
        private CardView cardSearchPeople;


        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPeopleSearchCover = itemView.findViewById(R.id.imgSearhAuthorCover);
            txtPeopleSearchName = itemView.findViewById(R.id.txtSearchAuthorName);
            cardSearchPeople = itemView.findViewById(R.id.cardSearhAuthor);

            cardSearchPeople.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (type) {
                case C.PEOPLE_TYPE_NORMAL :

                    int position = getAdapterPosition();
                    int userId = peopleList.get(position).getId();

                    FragmentOtherProfile fragmentOtherProfile = new FragmentOtherProfile(userId);

                    ((HomeActivity)context).loadFragment(fragmentOtherProfile, "oprosearch");

                    break;

                case C.PEOPLE_TYPE_GIFT :

                    int position2 = getAdapterPosition();
                    int userId2 = peopleList.get(position2).getId();
                    float price = Float.valueOf(book.getPrice());

                    PaymentFragment paymentFragment =
                            new PaymentFragment(price, book.getId(), null, C.PAID_TYPE_GIFT, userId2);

                    ((HomeActivity)context).loadFragment(paymentFragment, "paymentGifFrg");


                    break;
            }



        }
    }
}
