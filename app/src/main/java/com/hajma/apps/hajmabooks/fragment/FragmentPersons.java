package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.hajma.apps.hajmabooks.adapter.AuthorsAdapter;
import com.hajma.apps.hajmabooks.adapter.PeopleAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.AuthorApiModel;
import com.hajma.apps.hajmabooks.model.PeopleApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentPersons extends Fragment {

    private ImageButton imgBtnBackPersons;
    private TextView txtPersonsTitle;
    private RecyclerView rvAllPersons;
    private AuthorsAdapter authorsAdapter;
    private PeopleAdapter peopleAdapter;
    private int pagenumber;
    private int itemcount = 20;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading= true;
    private SwipeRefreshLayout swipeRefreshLayout;


    private ArrayList<AuthorApiModel> authors;
    private ArrayList<PeopleApiModel> peoples;
    private int type;

    private String query;
    private int langID;

    public FragmentPersons(int type, String query) {
        this.type = type;
        this.query = query;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_persons, container, false);

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


        //init variables
        swipeRefreshLayout = view.findViewById(R.id.swp_refresh_persons);
        imgBtnBackPersons = view.findViewById(R.id.imgBtnBackFromPersons);
        imgBtnBackPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        txtPersonsTitle = view.findViewById(R.id.txtTitlePersons);

        setTitle();

        rvAllPersons = view.findViewById(R.id.rvAllPersons);

        setupRecyclerView();


        return view;
    }

    private void setupRecyclerView() {

        switch (type) {
            case C.TYPE_ALL_AUTHOR :

                loadAuthors();

                break;

            case C.TYPE_ALL_PEOPLE :

                loadPeoples();

                break;

        }



    }

    private void loadPeoples() {

        peoples = new ArrayList<>();
        final RecyclerView.LayoutManager layoutManager
                = new GridLayoutManager(getActivity(), 3);

        peopleAdapter = new PeopleAdapter(getActivity(), peoples, C.PEOPLE_TYPE_NORMAL, null);

        rvAllPersons.setLayoutManager(layoutManager);
        rvAllPersons.setAdapter(peopleAdapter);

        //Pagination options
        pagenumber = 1;
        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                peoples.clear();
                pagenumber = 1;
                loading = true;
                peopleAdapter.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadRecyclerPeoples(pagenumber, query);
                }else {
                    //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if(isInternetAvailable()) {
            loadRecyclerPeoples(pagenumber, query);
        }else {
            //Toast.makeText(SeeAllBooksActivity.this, getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
        }

        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        rvAllPersons.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(oldScrollY < 0) {


                    visibleitemcount = layoutManager.getChildCount();
                    totalitemcount = layoutManager.getItemCount();
                    pastvisibleitems = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();

                    //if loading is true which means there is data to be fetched from the database
                    if(loading) {
                        if((visibleitemcount + pastvisibleitems) >= totalitemcount) {
                            swipeRefreshLayout.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            Log.e("pagetest","Page: "+pagenumber);
                            if(isInternetAvailable()) {
                                loadRecyclerPeoples(pagenumber, query);
                            }else {
                                //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });


    }

    private void loadAuthors() {

        authors = new ArrayList<>();
        final RecyclerView.LayoutManager layoutManager
                = new GridLayoutManager(getActivity(), 3);

        authorsAdapter = new AuthorsAdapter(getActivity(), authors);

        rvAllPersons.setLayoutManager(layoutManager);
        rvAllPersons.setAdapter(authorsAdapter);

        //Pagination options
        pagenumber = 1;
        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                authors.clear();
                pagenumber = 1;
                loading = true;
                authorsAdapter.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadRecyclerAuthors( langID, pagenumber, query);
                }else {
                    //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if(isInternetAvailable()) {
            loadRecyclerAuthors(langID, pagenumber, query);
        }else {
            //Toast.makeText(SeeAllBooksActivity.this, getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
        }

        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        rvAllPersons.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(oldScrollY < 0) {


                    visibleitemcount = layoutManager.getChildCount();
                    totalitemcount = layoutManager.getItemCount();
                    pastvisibleitems = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();

                    //if loading is true which means there is data to be fetched from the database
                    if(loading) {
                        if((visibleitemcount + pastvisibleitems) >= totalitemcount) {
                            swipeRefreshLayout.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            Log.e("pagetest","Page: "+pagenumber);
                            if(isInternetAvailable()) {
                                loadRecyclerAuthors(langID, pagenumber, query);
                            }else {
                                //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });


    }

    private void loadRecyclerAuthors(final int langID,final int page, String query) {

        String url = C.BASE_URL + String.format("/api/get-author?languageId=%1$s&page=%2$s&name=%3$s",
                langID, page, query);


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                    for(int i = 0;i < jsonArray.length();i++) {

                        JSONObject j = jsonArray.getJSONObject(i);

                        AuthorApiModel author = new AuthorApiModel();
                        author.setId(j.getInt("id"));
                        author.setName(j.getString("name"));
                        author.setProfile(j.getString("profile"));

                        authors.add(author);
                    }

                    if(!authors.isEmpty()) {
                        loading = true;
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Toast.makeText(getActivity(), "No load more data", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                authorsAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = MySingleton.getInstance(getActivity()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    private void loadRecyclerPeoples(final int page, String query) {

        String url = C.BASE_URL + String.format("/api/get-people?page=%1$s&name=%2$s",
                page, query);


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                    for(int i = 0;i < jsonArray.length();i++) {

                        JSONObject j = jsonArray.getJSONObject(i);

                        PeopleApiModel people = new PeopleApiModel();
                        people.setId(j.getInt("id"));
                        people.setName(j.getString("name"));
                        people.setProfile(j.getString("profile"));

                        peoples.add(people);
                    }

                    if(!peoples.isEmpty()) {
                        loading = true;
                        swipeRefreshLayout.setRefreshing(false);
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


    // set title
    private void setTitle() {

        switch (type) {
            case C.TYPE_ALL_AUTHOR :
                txtPersonsTitle.setText(getResources().getString(R.string._authors));
                break;

            case C.TYPE_ALL_PEOPLE :
                txtPersonsTitle.setText(getResources().getString(R.string._people));
                break;

        }

    }
}
