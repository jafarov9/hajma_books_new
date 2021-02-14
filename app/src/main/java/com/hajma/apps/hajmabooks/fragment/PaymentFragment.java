package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.CheckOutActivity;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.stripe.android.view.PaymentMethodsActivityStarter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class PaymentFragment extends Fragment {

    private ImageButton imageButtonBackFromPaymentFragment;
    private TextView txtTotalPrice;
    private Button btnNextToPaymentMethod;
    private float price;
    private ArrayList<BookApiModel> bookList;
    private int bookId;
    private int paidType;
    private int toUserId;

    public PaymentFragment(float price, int bookId, ArrayList<BookApiModel> bookList, int paidType, int toUserId) {
        this.price = price;
        this.bookList = bookList;
        this.bookId = bookId;
        this.paidType = paidType;
        this.toUserId = toUserId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment, container, false);

        txtTotalPrice = v.findViewById(R.id.txtTotalPrice);

        if(price != -1) {
            txtTotalPrice.setText(""+price);
        }else {

            float multiPrice = 0;
            for(int i = 0;i < bookList.size(); i++) {
                float temp = Float.valueOf(bookList.get(i).getPrice());
                multiPrice += temp;
            }

            price = multiPrice;
            txtTotalPrice.setText(""+price);
        }

        imageButtonBackFromPaymentFragment = v.findViewById(R.id.imageButtonBackFromPaymentFragment);
        imageButtonBackFromPaymentFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });



        btnNextToPaymentMethod = v.findViewById(R.id.btnNextToPaymentMethod);
        btnNextToPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), PaymentMethodsActivityStarter.class);
                //startActivity(intent);
                launchPaymentMethodsActivity();
            }
        });

        return v;
    }

    private void launchPaymentMethodsActivity() {
        EventBus.getDefault().postSticky(new DataEvent.CallPayInformation(bookId, paidType, toUserId));
        new PaymentMethodsActivityStarter(getActivity()).startForResult();
    }


}
