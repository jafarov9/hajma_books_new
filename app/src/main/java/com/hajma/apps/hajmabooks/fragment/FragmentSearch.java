package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.adapter.AudioBooksAdapter;
import com.hajma.apps.hajmabooks.adapter.AuthorsAdapter;
import com.hajma.apps.hajmabooks.adapter.NewBooksAdapter;
import com.hajma.apps.hajmabooks.adapter.PeopleAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.AuthorApiModel;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.model.PeopleApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSearch extends Fragment implements SearchView.OnQueryTextListener , View.OnClickListener {


    private TextView txtSeeAllBooksSearch, txtSeeAllAudioBooksSearch,
            txtSeeAllAuthoursSearch, txtSeeAllPeoplesSearch;


    private SearchView searchView;
    private RecyclerView rv_all_books_search;
    private RecyclerView rv_audio_books_search;
    private RecyclerView rv_authors_search;
    private RecyclerView rv_peoples_search;
    private NewBooksAdapter booksAdapter;
    private AudioBooksAdapter audioBooksAdapter;
    private ArrayList<AuthorApiModel> authorsList;
    private ArrayList<BookApiModel> bookList;
    private NewBooksAdapter searchBooksAdapter;
    private ArrayList<BookApiModel> audioBookList;
    private ArrayList<PeopleApiModel> peopleList;
    private PeopleAdapter peopleAdapter;
    private String searchWord = "";
    private int langID;

    private AuthorsAdapter authorsAdapter;
    private Handler mHandler = new Handler();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

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
        txtSeeAllBooksSearch = view.findViewById(R.id.txtSeeAllBooksSearch);
        txtSeeAllAudioBooksSearch = view.findViewById(R.id.txtSeeAllAudioBooksSearch);
        txtSeeAllAuthoursSearch = view.findViewById(R.id.txtSeeAllAuthoursSearch);
        txtSeeAllPeoplesSearch = view.findViewById(R.id.txtSeeAllPeoplesSearch);

        //set onclick listener for textViews
        txtSeeAllBooksSearch.setOnClickListener(this);
        txtSeeAllPeoplesSearch.setOnClickListener(this);
        txtSeeAllAuthoursSearch.setOnClickListener(this);
        txtSeeAllAudioBooksSearch.setOnClickListener(this);


        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
        rv_all_books_search = view.findViewById(R.id.rv_all_books_search);
        rv_audio_books_search = view.findViewById(R.id.rv_all_audio_books_search);
        rv_authors_search = view.findViewById(R.id.rv_all_authors_search);
        rv_peoples_search = view.findViewById(R.id.rv_all_peoples_search);

        //setup recyclerviews adapter and layouts
        setupRecyclerViews();



        return view;
    }

    private void setupRecyclerViews() {

        LinearLayoutManager layoutManagerAllBooks =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerAudioBooks =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerAuthors =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerPeoples =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);


        //all books
        rv_all_books_search.setLayoutManager(layoutManagerAllBooks);
        bookList = new ArrayList<>();
        booksAdapter = new NewBooksAdapter(getActivity(), bookList);

        //audio books
        rv_audio_books_search.setLayoutManager(layoutManagerAudioBooks);
        audioBookList = new ArrayList<>();
        audioBooksAdapter = new AudioBooksAdapter(getActivity(), audioBookList);

        //authors
        rv_authors_search.setLayoutManager(layoutManagerAuthors);
        authorsList = new ArrayList<>();
        authorsAdapter = new AuthorsAdapter(getActivity(), authorsList);

        //Peoples
        rv_peoples_search.setLayoutManager(layoutManagerPeoples);
        peopleList = new ArrayList<>();
        peopleAdapter = new PeopleAdapter(getActivity(), peopleList, C.PEOPLE_TYPE_NORMAL, null);

        //set adapters
        rv_all_books_search.setAdapter(booksAdapter);
        rv_audio_books_search.setAdapter(audioBooksAdapter);
        rv_authors_search.setAdapter(authorsAdapter);
        rv_peoples_search.setAdapter(peopleAdapter);



        loadAllSearchedRecyclerViewData(langID, 1, searchWord);
    }

    private void loadAllSearchedRecyclerViewData(final int langID, final int page, String searchWord) {

        bookList.clear();
        audioBookList.clear();
        authorsList.clear();
        peopleList.clear();
        booksAdapter.notifyDataSetChanged();
        audioBooksAdapter.notifyDataSetChanged();
        peopleAdapter.notifyDataSetChanged();
        authorsAdapter.notifyDataSetChanged();


        String url = C.BASE_URL +
                String.format("/api/common-search?languageId=%1$s&page=%2$s&name=%3$s",
                        langID, page, searchWord);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArraySearchBooks = jsonObject.getJSONObject("success").getJSONArray("book");
                    JSONArray jsonArraySearchAudioBooks = jsonObject.getJSONObject("success").getJSONArray("audio_book");
                    JSONArray jsonArraySearchAuthors = jsonObject.getJSONObject("success").getJSONArray("author");
                    JSONArray jsonArraySearchPeople = jsonObject.getJSONObject("success").getJSONArray("people");

                    //add search book list
                    for(int i = 0;i < jsonArraySearchBooks.length();i++) {
                        JSONObject j = jsonArraySearchBooks.getJSONObject(i);
                        BookApiModel book = new BookApiModel();
                        book.setId(j.getInt("id"));
                        book.setCover(j.getString("cover"));
                        book.setName(j.getString("name"));
                        book.setPageCount(j.getInt("page_count"));
                        book.setSound_count(j.getInt("sound_count"));
                        book.setPrice(j.getString("price"));
                        book.setYear(j.getInt("year"));
                        bookList.add(book);
                    }

                    //add search audio books list
                    for(int i = 0;i < jsonArraySearchAudioBooks.length();i++) {
                        JSONObject j = jsonArraySearchAudioBooks.getJSONObject(i);
                        BookApiModel book = new BookApiModel();
                        book.setId(j.getInt("id"));
                        book.setCover(j.getString("cover"));
                        book.setName(j.getString("name"));
                        book.setPageCount(j.getInt("page_count"));
                        book.setSound_count(j.getInt("sound_count"));
                        book.setPrice(j.getString("price"));
                        book.setYear(j.getInt("year"));
                        audioBookList.add(book);
                    }


                    //add authors list
                    for(int i = 0;i < jsonArraySearchAuthors.length();i++) {
                        JSONObject j = jsonArraySearchAuthors.getJSONObject(i);
                        AuthorApiModel author = new AuthorApiModel();
                        author.setId(j.getInt("id"));
                        author.setName(j.getString("name"));
                        author.setProfile(j.getString("profile"));
                        authorsList.add(author);
                    }

                    //add search people list
                    for(int i = 0;i < jsonArraySearchPeople.length();i++) {
                        JSONObject j = jsonArraySearchPeople.getJSONObject(i);
                        PeopleApiModel people = new PeopleApiModel();
                        people.setId(j.getInt("id"));
                        people.setName(j.getString("name"));
                        people.setProfile(j.getString("profile"));
                        peopleList.add(people);
                    }

                    booksAdapter.notifyDataSetChanged();
                    audioBooksAdapter.notifyDataSetChanged();
                    authorsAdapter.notifyDataSetChanged();
                    peopleAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mHandler.removeCallbacksAndMessages(null);
        searchWord = query;


        Log.e("bvbv", "Query" + query);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAllSearchedRecyclerViewData(langID, 1, query);
            }
        }, 300);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchWord = newText;

        if(newText.equals("")) {
            mHandler.removeCallbacksAndMessages(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadAllSearchedRecyclerViewData(langID, 1, newText);
                }
            }, 300);
            Log.e("cvb", newText);

        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtSeeAllBooksSearch :

                SeeAllBooksFragment seeSearchedBooks
                            = new SeeAllBooksFragment(C.TYPE_NEW, null, -1, -1, null, null, searchWord, -1);

                ((HomeActivity)getActivity()).loadFragment(seeSearchedBooks, "searchBooksFrg");
                break;

            case R.id.txtSeeAllAudioBooksSearch :

                SeeAllBooksFragment seeSearchAudio =
                        new SeeAllBooksFragment(C.TYPE_SEARCH_AUDIO_BOOK, audioBookList, -1,-1, null, null, searchWord, -1);

                ((HomeActivity)getActivity()).loadFragment(seeSearchAudio, "searchAudioBooksFrg");

                break;

            case R.id.txtSeeAllAuthoursSearch :

                FragmentPersons fragmentAuthors
                        = new FragmentPersons(C.TYPE_ALL_AUTHOR, searchWord);

                ((HomeActivity)getActivity()).loadFragment(fragmentAuthors, "searchAuthorsFrg");


                break;

            case R.id.txtSeeAllPeoplesSearch :

                FragmentPersons fragmenPeoples
                        = new FragmentPersons(C.TYPE_ALL_PEOPLE, searchWord);

                ((HomeActivity)getActivity()).loadFragment(fragmenPeoples, "searchPeoplesFrg");


                break;

        }

    }
}
