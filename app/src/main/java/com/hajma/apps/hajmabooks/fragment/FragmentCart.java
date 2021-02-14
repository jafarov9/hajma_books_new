package com.hajma.apps.hajmabooks.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.adapter.CartAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCart extends Fragment {

    private RecyclerView rv_shopping_cart;
    private CartAdapter adapter;
    private ArrayList<BookApiModel> myCardList;
    private UserDAOInterface userDIF;
    private Button btnBuyAllCartItems;
    private String token;
    private int langID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        String language = LocaleHelper.getPersistedData(getActivity(), "az");

        if(language.equals("az")) {
            langID = C.LANGUAGE_AZ;
        } else if(language.equals("en")) {
            langID = C.LANGUAGE_EN;
        }else if(language.equals("ru")) {
            langID = C.LANGUAGE_RU;
        }else {
            langID = C.LANGUAGE_AZ;
        }



        btnBuyAllCartItems = view.findViewById(R.id.btnBuyAllCartItems);

        token = getActivity()
                .getSharedPreferences("usercontrol", Context.MODE_PRIVATE)
                .getString("token", null);

        userDIF = ApiUtils.getUserDAOInterface();

        rv_shopping_cart = view.findViewById(R.id.rv_cart_all);
        setupRecycerView();
        loadDataRecyclerView(langID, token);

        Log.e("jjj", myCardList.size()+"");


        btnBuyAllCartItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFragment paymentFragment = new PaymentFragment(-1, -1 , myCardList, C.PAID_TYPE_MULTIPLE, 0);
                ((HomeActivity)getActivity()).loadFragment(paymentFragment, "multiPayment");
            }
        });

        return view;
    }

    private void setupRecycerView() {
        myCardList = new ArrayList<>();

        rv_shopping_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CartAdapter(getActivity(), myCardList);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_shopping_cart);
        rv_shopping_cart.setAdapter(adapter);

    }

    private void loadDataRecyclerView(int langID, String token) {

        myCardList.clear();

        RequestBody langBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(langID));

        userDIF.postListMyCart(langBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {

                    try {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s).getJSONObject("success");

                        JSONArray data = jsonObject.getJSONArray("data");

                        //add data to my cart list
                        for(int i = 0;i < data.length();i++) {

                            JSONObject j = data.getJSONObject(i);
                            BookApiModel bookApiModel = new BookApiModel();
                            bookApiModel.setId(j.getInt("id"));
                            bookApiModel.setName(j.getString("name"));
                            bookApiModel.setCover(j.getString("cover"));
                            bookApiModel.setPrice(j.getString("price"));
                            bookApiModel.setYear(j.getInt("year"));
                            bookApiModel.setAuthor_name(j.getString("author_name"));
                            bookApiModel.setPage_count(j.getInt("page_count"));
                            bookApiModel.setSound_count(j.getInt("sound_count"));

                            myCardList.add(bookApiModel);

                        }

                        if(myCardList.isEmpty()) {
                            btnBuyAllCartItems.setVisibility(View.GONE);
                        }else {
                            btnBuyAllCartItems.setVisibility(View.VISIBLE);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    //swipe to remove from card item
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (isInternetAvialible()){
                removeItemFromApi(viewHolder);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                    .addActionIcon(R.drawable.removeicon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



    private void removeItemFromApi(RecyclerView.ViewHolder viewHolder) {

        int position = viewHolder.getAdapterPosition();
        int bookId = myCardList.get(position).getId();

        RequestBody bookIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bookId));

        userDIF.postRemoveMyCard(bookIdBody, "Bearer " + token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.e("ozu", "Onresponse");


                if(response.isSuccessful()) {

                    Log.e("ozu", "Success");

                    myCardList.remove(position);
                    adapter.notifyDataSetChanged();

                    if(myCardList.isEmpty()) {
                        btnBuyAllCartItems.setVisibility(View.GONE);
                    }else {
                        btnBuyAllCartItems.setVisibility(View.VISIBLE);
                    }
                }else {

                    try {
                        Log.e("ozu", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ozu", t.getMessage());
                return;
            }
        });

    }

    private boolean isInternetAvialible() {
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void OnCartUpdate(DataEvent.CallCartUpdate event) {
        if(event.getResult() == 1) {
            loadDataRecyclerView(langID, token);
        }
    }
}
