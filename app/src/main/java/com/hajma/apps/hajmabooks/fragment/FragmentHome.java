package com.hajma.apps.hajmabooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.Variables;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
//import com.hajma.apps.hajmabook.activity.SeeAllBooksActivity;
import com.hajma.apps.hajmabooks.adapter.AudioBooksAdapter;
import com.hajma.apps.hajmabooks.adapter.CategoryLargeAdapter;
import com.hajma.apps.hajmabooks.adapter.CollectionsLargeAdapter;
import com.hajma.apps.hajmabooks.adapter.ForYouBooksAdapter;
import com.hajma.apps.hajmabooks.adapter.NewBooksAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.model.CategoryApiModel;
import com.hajma.apps.hajmabooks.model.CollectionApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private RecyclerView rv_new_books;
    private RecyclerView rv_for_you_books;
    private RecyclerView rv_collections;
    private RecyclerView rv_audio_books;
    private RecyclerView rv_best_seller_books;
    private RecyclerView rv_category;
    private RecyclerView rv_free_books;
    private RecyclerView rv_free_audio_books;
    private AudioBooksAdapter freeAudioBooksAdapter;
    private NewBooksAdapter newBooksAdapter;
    private ForYouBooksAdapter forYouBooksAdapter;
    private CollectionsLargeAdapter collectionsLargeAdapter;
    private AudioBooksAdapter audioBooksAdapter;
    private NewBooksAdapter bestSellerAdapter;
    private NewBooksAdapter freeBooksAdapter;
    private CategoryLargeAdapter categoryLargeAdapter;
    private FragmentAllCollections fragmentAllCollections = null;

    private ProgressBar progressBarNewBooks;
    private ProgressBar progressBarForYouBooks;
    private ProgressBar progressBarCollections;


    private TextView txtSeeAllNewBooks, txtSeeAllForYouBooks, txtSeeAllCollections,
        txtSeeAllAudioBooks, txtSeeAllBestSellers, txtSeeAllCategories, txtSeeAllFreeBooks;
    private TextView txtSeeALlFreeAudioBooks;

    private ArrayList<BookApiModel> newBooksList, forYouBooksList,
            bestSellerBooksList, audioBooksList, freeBookList;
    private ArrayList<CollectionApiModel> collectionList;
    private ArrayList<CategoryApiModel> categoryList;
    private ArrayList<BookApiModel> freeAudioBookList;
    private int langID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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


        //TextViews
        txtSeeAllNewBooks = view.findViewById(R.id.txtSeeAllNewBooks);
        txtSeeAllForYouBooks = view.findViewById(R.id.txtSeeAllForYouBooks);
        txtSeeAllAudioBooks = view.findViewById(R.id.txtSeeAllAudioBooks);
        txtSeeAllBestSellers = view.findViewById(R.id.txtSeeAllBestSeller);
        txtSeeAllFreeBooks = view.findViewById(R.id.txtSeeAllFreeBooks);
        txtSeeAllCollections = view.findViewById(R.id.txtSeeAllCollections);
        txtSeeAllCategories = view.findViewById(R.id.txtSeeAllCategories);
        txtSeeALlFreeAudioBooks = view.findViewById(R.id.txtSeeAllFreeAudioBooks);

        //progress bars init
        progressBarNewBooks = view.findViewById(R.id.progressNewBooks);
        progressBarForYouBooks = view.findViewById(R.id.progressForYou);
        progressBarCollections = view.findViewById(R.id.progressCollections);


        //Add onclick listener to all see all labels
        txtSeeAllNewBooks.setOnClickListener(this);
        txtSeeAllForYouBooks.setOnClickListener(this);
        txtSeeAllAudioBooks.setOnClickListener(this);
        txtSeeAllBestSellers.setOnClickListener(this);
        txtSeeAllFreeBooks.setOnClickListener(this);
        txtSeeAllCollections.setOnClickListener(this);
        txtSeeAllCategories.setOnClickListener(this);
        txtSeeALlFreeAudioBooks.setOnClickListener(this);



        //Recyclerview initialization
        rv_new_books = view.findViewById(R.id.new_books_recycleview);
        rv_for_you_books = view.findViewById(R.id.for_you_books_recycleview);
        rv_collections = view.findViewById(R.id.collections_recycleview);
        rv_audio_books = view.findViewById(R.id.audio_books_recycleview);
        rv_best_seller_books = view.findViewById(R.id.best_seller_recycleview);
        rv_category = view.findViewById(R.id.category_recycleview);
        rv_free_books = view.findViewById(R.id.free_books_recycleview);
        rv_free_audio_books = view.findViewById(R.id.free_audio_books_recycleview);


        setRecyclerViewNewBooks();
        setRecyclerViewForYouBooks();
        setRecyclerViewCollections();
        setRecyclerViewAudioBooks();
        setRecyclerViewBestSellerBooks();
        setRecyclerViewCategories();
        setRecyclerViewFreeBooks();
        setRecyclerViewFreeAudioBooks();


        //Load all recyclerview data
        if(isInternetAvialible()) {
            loadRecyclerViewMainData(langID);
        }



        return view;
    }

    //set recyclerview for new books
    private void setRecyclerViewNewBooks() {
        newBooksList = new ArrayList<>();
        newBooksAdapter = new NewBooksAdapter(getActivity(), newBooksList);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_new_books.setLayoutManager(layoutManager2);
        rv_new_books.setAdapter(newBooksAdapter);
    }

    //Set recyclerview for you books
    private void setRecyclerViewForYouBooks() {
        forYouBooksList = new ArrayList<>();
        forYouBooksAdapter = new ForYouBooksAdapter(getActivity(), forYouBooksList);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_for_you_books.setLayoutManager(layoutManager2);
        rv_for_you_books.setAdapter(forYouBooksAdapter);
    }

    //Set recyclerview Collections
    private void setRecyclerViewCollections() {
        collectionList = new ArrayList<>();
        collectionsLargeAdapter = new CollectionsLargeAdapter(getActivity(), collectionList);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_collections.setLayoutManager(layoutManager2);
        rv_collections.setAdapter(collectionsLargeAdapter);
    }

    private void setRecyclerViewAudioBooks() {
        audioBooksList = new ArrayList<>();
        audioBooksAdapter = new AudioBooksAdapter(getActivity(), audioBooksList);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_audio_books.setLayoutManager(layoutManager2);
        rv_audio_books.setAdapter(audioBooksAdapter);
    }

    //set recyclerview for free books
    private void setRecyclerViewFreeBooks() {
        freeBookList = new ArrayList<>();
        freeBooksAdapter = new NewBooksAdapter(getActivity(), freeBookList);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_free_books.setLayoutManager(layoutManager2);
        rv_free_books.setAdapter(freeBooksAdapter);

    }

    //set recyclerview for free audio books
    private void setRecyclerViewFreeAudioBooks() {
        freeAudioBookList = new ArrayList<>();
        freeAudioBooksAdapter = new AudioBooksAdapter(getActivity(), freeAudioBookList);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_free_audio_books.setLayoutManager(layoutManager2);
        rv_free_audio_books.setAdapter(freeAudioBooksAdapter);

    }

    //set recyclerview best seller books
    private void setRecyclerViewBestSellerBooks() {
        bestSellerBooksList = new ArrayList<>();
        bestSellerAdapter = new NewBooksAdapter(getActivity(), bestSellerBooksList);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_best_seller_books.setLayoutManager(layoutManager2);
        rv_best_seller_books.setAdapter(bestSellerAdapter);
    }

    //set recyclerview categories
    private void setRecyclerViewCategories() {
        categoryList = new ArrayList<>();
        categoryLargeAdapter = new CategoryLargeAdapter(getActivity(), categoryList);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_category.setLayoutManager(layoutManager2);
        rv_category.setAdapter(categoryLargeAdapter);
    }


    /*//Load recyclerview data for new books
    private void loadRecyclerViewNewBooks(final int lang_id, final int page) {

        String url = C.BASE_URL +
                String.format("/api/search-books-by-name?languageId=%1$s&page=%2$s&name=",
                        lang_id, page);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));

                                Log.e("sdsd", book.getName());

                                newBooksList.add(book);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        newBooksAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    //Load recyclerview data for you books
    private void loadRecyclerViewForYouBooks(final int lang_id) {

        String url = C.BASE_URL +
                String.format("/api/recommended?languageId=%1$s",
                        lang_id);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));


                                forYouBooksList.add(book);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        forYouBooksAdapter.notifyDataSetChanged();
                        forYouBooksAdapter.setSub(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    //Load recyclerview data for collections
    private void loadRecyclerViewCategories(final int lang_id) {

        String url = C.BASE_URL +
                String.format("/api/get-category?languageId=%1$s",
                        lang_id);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                CategoryApiModel category = new CategoryApiModel();
                                category.setHorizontal_large(j.getString("horizontal_large"));
                                category.setHorizontal_small(j.getString("horizontal_small"));
                                category.setId(j.getInt("id"));
                                category.setName(j.getString("name"));
                                category.setVertical(j.getString("vertical"));

                                categoryList.add(category);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        categoryLargeAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    //load recyclervew data for category
    private void loadRecyclerViewCollections(final int lang_id) {

        String url = C.BASE_URL +
                String.format("/api/get-collection?languageId=%1$s",
                        lang_id);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                CollectionApiModel collection = new CollectionApiModel();
                                collection.setHorizontal_large(j.getString("horizontal_large"));
                                collection.setHorizontal_small(j.getString("horizontal_small"));
                                collection.setId(j.getInt("id"));
                                collection.setName(j.getString("name"));
                                collection.setVertical(j.getString("vertical"));

                                collectionList.add(collection);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        collectionsLargeAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    //Load recyclerview data for best seller books
    private void loadRecyclerViewBestSellerBooks(final int lang_id) {

        String url = C.BASE_URL +
                String.format("/api/get-best-seller?languageId=%1$s",
                        lang_id);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for(int i = 0;i < jsonArray.length(); i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                bestSellerBooksList.add(book);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        bestSellerAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    //Load recyclerview data for free books
    private void loadRecyclerViewFreeBooks(final int lang_id, final int page) {

        String url = C.BASE_URL +
                String.format("/api/free-books?languageId=%1$s&page=%2$s",
                        lang_id, page);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));

                                Log.e("sdsd", book.getName());

                                freeBookList.add(book);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        freeBooksAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }*/

    private void loadRecyclerViewMainData(final int langID) {

        String url = C.BASE_URL +
                String.format("/api/main-data?languageId=%1$s",
                        langID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArrayNewBooks = jsonObject.getJSONObject("success").getJSONArray("new_books");
                            JSONArray jsonArrayForYouBooks = jsonObject.getJSONObject("success").getJSONArray("for_you");
                            JSONArray jsonArrayCollections = jsonObject.getJSONObject("success").getJSONArray("collection");
                            JSONArray jsonArrayBestSeller = jsonObject.getJSONObject("success").getJSONArray("best_seller");
                            JSONArray jsonArrayCategory = jsonObject.getJSONObject("success").getJSONArray("category");
                            JSONArray jsonArrayFreeBooks = jsonObject.getJSONObject("success").getJSONArray("free_books");
                            JSONArray jsonArrayAudioBooks = jsonObject.getJSONObject("success").getJSONArray("audio_books");
                            JSONArray jsonArrayFreeAudioBooks = jsonObject.getJSONObject("success").getJSONArray("free_books_with_audio");



                            //get json data for free books
                            for(int i = 0;i < jsonArrayNewBooks.length();i++) {
                                JSONObject j = jsonArrayNewBooks.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setSound_count(j.getInt("sound_count"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                newBooksList.add(book);
                            }

                            //get json data foryou books
                            for(int i = 0;i < jsonArrayForYouBooks.length();i++) {
                                JSONObject j = jsonArrayForYouBooks.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setSound_count(j.getInt("sound_count"));
                                book.setName(j.getString("name"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                forYouBooksList.add(book);
                            }

                            //get json data collections
                            for(int i = 0;i < jsonArrayCollections.length();i++) {
                                JSONObject j = jsonArrayCollections.getJSONObject(i);
                                CollectionApiModel collection = new CollectionApiModel();
                                collection.setHorizontal_large(j.getString("horizontal_large"));
                                collection.setHorizontal_small(j.getString("horizontal_small"));
                                collection.setId(j.getInt("id"));
                                collection.setName(j.getString("name"));
                                collection.setVertical(j.getString("vertical"));
                                collectionList.add(collection);
                            }

                            //get json data collections
                            for(int i = 0;i < jsonArrayBestSeller.length();i++) {
                                JSONObject j = jsonArrayBestSeller.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setSound_count(j.getInt("sound_count"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                bestSellerBooksList.add(book);
                            }

                            //get json data categories
                            for(int i = 0;i < jsonArrayCategory.length();i++) {
                                JSONObject j = jsonArrayCategory.getJSONObject(i);
                                CategoryApiModel category = new CategoryApiModel();
                                category.setHorizontal_large(j.getString("horizontal_large"));
                                category.setHorizontal_small(j.getString("horizontal_small"));
                                category.setId(j.getInt("id"));
                                category.setName(j.getString("name"));
                                category.setVertical(j.getString("vertical"));
                                categoryList.add(category);
                            }

                            //get json data free books
                            for(int i = 0;i < jsonArrayFreeBooks.length();i++) {
                                JSONObject j = jsonArrayFreeBooks.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setSound_count(j.getInt("sound_count"));
                                book.setName(j.getString("name"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                freeBookList.add(book);
                            }

                            //get json data audio books
                            for(int i = 0;i < jsonArrayAudioBooks.length();i++) {
                                JSONObject j = jsonArrayAudioBooks.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setSound_count(j.getInt("sound_count"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                audioBooksList.add(book);
                            }

                            //get json data free audio books
                            for(int i = 0;i < jsonArrayFreeAudioBooks.length();i++) {
                                JSONObject j = jsonArrayFreeAudioBooks.getJSONObject(i);
                                BookApiModel book = new BookApiModel();
                                book.setId(j.getInt("id"));
                                book.setCover(j.getString("cover"));
                                book.setName(j.getString("name"));
                                book.setSound_count(j.getInt("sound_count"));
                                book.setPageCount(j.getInt("page_count"));
                                book.setPrice(j.getString("price"));
                                book.setYear(j.getInt("year"));
                                freeAudioBookList.add(book);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //hide progress bars
                        progressBarNewBooks.setIndeterminate(false);
                        progressBarNewBooks.setVisibility(View.GONE);

                        progressBarForYouBooks.setIndeterminate(false);
                        progressBarForYouBooks.setVisibility(View.GONE);

                        progressBarCollections.setIndeterminate(false);
                        progressBarCollections.setVisibility(View.GONE);




                        newBooksAdapter.notifyDataSetChanged();
                        forYouBooksAdapter.notifyDataSetChanged();
                        collectionsLargeAdapter.notifyDataSetChanged();
                        bestSellerAdapter.notifyDataSetChanged();
                        categoryLargeAdapter.notifyDataSetChanged();
                        freeBooksAdapter.notifyDataSetChanged();
                        audioBooksAdapter.notifyDataSetChanged();
                        freeAudioBooksAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.txtSeeAllNewBooks :
                Log.e("zxzx", "Burdayam");
                /*Intent intent = new Intent(getActivity(), SeeAllBooksActivity.class);
                intent.putExtra("type", C.TYPE_NEW);
                startActivity(intent);*/

                SeeAllBooksFragment seeAllBooksFragment = new SeeAllBooksFragment(C.TYPE_NEW, null, - 1, -1, null, null, "", -1);
                loadFragment(seeAllBooksFragment, "seeallbooks");

                break;

            case R.id.txtSeeAllForYouBooks :
                Log.e("zxzx", "Burdayam");

                SeeAllBooksFragment seeAllForYou = new SeeAllBooksFragment(C.TYPE_FOR_YOU, forYouBooksList, -1 , -1, null, null, "", -1);
                loadFragment(seeAllForYou, "seeallbooks");

                break;

            case R.id.txtSeeAllBestSeller :
                Log.e("zxzx", "Burdayam");

                SeeAllBooksFragment seeAllBestSeller = new SeeAllBooksFragment(C.TYPE_BESTSELLER, bestSellerBooksList, -1 , -1, null, null, "", -1);
                loadFragment(seeAllBestSeller, "seeallbooks");

                break;

            case R.id.txtSeeAllFreeBooks :
                Log.e("zxzx", "Burdayam");
                SeeAllBooksFragment seeAllFreeBooks = new SeeAllBooksFragment(C.TYPE_FREE, null, -1, - 1, null, null, "", -1);
                loadFragment(seeAllFreeBooks, "seeallbooks");
                break;

            case R.id.txtSeeAllCollections :

                fragmentAllCollections = new FragmentAllCollections();
                loadFragment(fragmentAllCollections, "frgcoll");

                break;

            case R.id.txtSeeAllCategories:

                EventBus.getDefault().post(new DataEvent.CallViewFragment(1));
                break;

            case R.id.txtSeeAllAudioBooks :

                SeeAllBooksFragment seeAllAudio = new SeeAllBooksFragment(C.TYPE_AUDIO, null, -1, - 1, null, null, "", -1);
                loadFragment(seeAllAudio, "seeallbooks");

                break;

            case R.id.txtSeeAllFreeAudioBooks :

                SeeAllBooksFragment seeAllFreeAudio = new SeeAllBooksFragment(C.TYPE_AUDIO_FREE, null, -1, - 1, null, null, "", -1);
                loadFragment(seeAllFreeAudio, "seeallbooks");

                break;


        }

    }

    private void loadFragment(Fragment fragment, String tag) {
        if (fragment == null || getActivity() == null)
            return;
        ((HomeActivity) getActivity()).loadFragment(fragment, tag);

    }

    private boolean isInternetAvialible() {

        return Variables.isNetworkConnected;

    }

}
