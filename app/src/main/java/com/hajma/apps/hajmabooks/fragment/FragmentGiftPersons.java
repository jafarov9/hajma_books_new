package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.PeopleAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.DetailedBookApiModel;
import com.hajma.apps.hajmabooks.model.PeopleApiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentGiftPersons extends Fragment implements SearchView.OnQueryTextListener {


    private SearchView searchViewGift;
    private RecyclerView rvGiftPersons;
    private PeopleAdapter peopleAdapter;
    private ArrayList<PeopleApiModel> peopleList;
    private SwipeRefreshLayout swpGiftPersons;
    private int pagenumber;
    private int itemcount = 20;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading= true;
    private String searchText = "";
    private DetailedBookApiModel book;

    public FragmentGiftPersons(DetailedBookApiModel book) {
        this.book = book;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send_gift, container, false);



        //init variables
        searchViewGift = view.findViewById(R.id.search_view_gift);
        searchViewGift.setOnQueryTextListener(this);
        swpGiftPersons = view.findViewById(R.id.swpGiftPersons);
        rvGiftPersons = view.findViewById(R.id.rvGiftPersons);

        setupRecyclerView();


        return view;
    }

    private void setupRecyclerView() {

        peopleList = new ArrayList<>();

        final RecyclerView.LayoutManager layoutManager
                = new GridLayoutManager(getActivity(), 3);

        rvGiftPersons.setLayoutManager(layoutManager);
        peopleAdapter = new PeopleAdapter(getActivity(), peopleList, C.PEOPLE_TYPE_GIFT, book);

        rvGiftPersons.setAdapter(peopleAdapter);

        //Pagination options
        pagenumber = 1;
        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swpGiftPersons.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                peopleList.clear();
                pagenumber = 1;
                loading = true;
                peopleAdapter.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadRecyclerPeoples(pagenumber, searchText);
                }else {
                    //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                    swpGiftPersons.setRefreshing(false);
                }
            }
        });

        if(isInternetAvailable()) {
            loadRecyclerPeoples(pagenumber, searchText);
        }else {
            //Toast.makeText(SeeAllBooksActivity.this, getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
        }

        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        rvGiftPersons.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(oldScrollY < 0) {


                    visibleitemcount = layoutManager.getChildCount();
                    totalitemcount = layoutManager.getItemCount();
                    pastvisibleitems = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();

                    //if loading is true which means there is data to be fetched from the database
                    if(loading) {
                        if((visibleitemcount + pastvisibleitems) >= totalitemcount) {
                            swpGiftPersons.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            Log.e("pagetest","Page: "+pagenumber);
                            if(isInternetAvailable()) {
                                loadRecyclerPeoples(pagenumber, searchText);
                            }else {
                                //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

    }


    private void loadRecyclerPeoples(final int page, String query) {



        String url = C.BASE_URL + String.format("/api/get-people?page=%1$s&name=%2$s",
                page, query);


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swpGiftPersons.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                    for(int i = 0;i < jsonArray.length();i++) {

                        JSONObject j = jsonArray.getJSONObject(i);

                        PeopleApiModel people = new PeopleApiModel();
                        people.setId(j.getInt("id"));
                        people.setName(j.getString("name"));
                        people.setProfile(j.getString("profile"));

                        peopleList.add(people);
                    }

                    if(!peopleList.isEmpty()) {
                        loading = true;
                        swpGiftPersons.setRefreshing(false);
                    } else {
                        Toast.makeText(getActivity(), "No load more data", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                peopleAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = MySingleton.getInstance(getActivity()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    private boolean isInternetAvailable() {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        peopleList.clear();
        pagenumber = 1;
        loading = true;
        peopleAdapter.notifyDataSetChanged();

        searchText = query;

        Log.e("hjhj", "Burdayam "+ searchText);

        loadRecyclerPeoples(1, searchText);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.equals("")) {
            peopleList.clear();
            pagenumber = 1;
            loading = true;
            peopleAdapter.notifyDataSetChanged();

            loadRecyclerPeoples(1, newText);

        }

        return true;
    }
}
