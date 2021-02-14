package com.hajma.apps.hajmabooks.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.adapter.SeeAllBooksAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SeeAllBooksFragment extends Fragment {

    private ImageButton imageButtonBackFromSeeAllBooks;
    private TextView txtTitleFromBooks;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_see_all_books;
    private SeeAllBooksAdapter seeAllBooksAdapter;
    private ArrayList<BookApiModel> bookList = new ArrayList<>();
    private int pagenumber;
    private int itemcount = 20;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading= true;
    private int type;
    private int collID;
    private int categoryID;
    private String collTitle;
    private String catTitle;
    private String query;
    private int authorId;
    private int langID;

    public SeeAllBooksFragment(int type, ArrayList<BookApiModel> tempList, int collID, int categoryID, String collTitle, String catTitle, String query, int authorId) {
        this.type = type;
        this.collID = collID;
        this.categoryID = categoryID;
        this.catTitle = catTitle;
        this.collTitle = collTitle;
        this.query = query;
        this.authorId = authorId;

        if(tempList != null) {
            bookList = tempList;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_see_all_books, container, false);

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


        txtTitleFromBooks = view.findViewById(R.id.txtTitleSeeAllBooks);
        setTitleFromType();

        swipeRefreshLayout = view.findViewById(R.id.swp_refresh_see_all);
        rv_see_all_books = view.findViewById(R.id.rv_see_all_books);
        setupRecyclerView(type);

        imageButtonBackFromSeeAllBooks = view.findViewById(R.id.imageButtonSeeALlBooksBack);

        imageButtonBackFromSeeAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        return view;
    }



    private void setTitleFromType() {

        switch (type) {
            case 6 : //type new
                txtTitleFromBooks.setText(getResources().getString(R.string.new_books));
                break;

            case 5 : //type for you
                txtTitleFromBooks.setText(getResources().getString(R.string.for_you));
                break;

            case 4 : //type bestseller
                txtTitleFromBooks.setText(getResources().getString(R.string.best_seller));
                break;

            case 101:

            case 3 : //type audio
                txtTitleFromBooks.setText(getResources().getString(R.string.audio_books));
                break;

            case 0 : //type free
                txtTitleFromBooks.setText(getResources().getString(R.string.free_books));
                break;

            case 11: //type collections
                txtTitleFromBooks.setText(collTitle);
                break;

            case 12: //for categories
                txtTitleFromBooks.setText(catTitle);
                break;

            case 21: //for free audio books
                txtTitleFromBooks.setText(getResources().getString(R.string._free_audio_books));
                break;

            case 19://for author books
                txtTitleFromBooks.setText("Author books");
                break;

        }

    }

    private void setupRecyclerView(final int type) {

        seeAllBooksAdapter = new SeeAllBooksAdapter(getActivity(), bookList, C.OTHER_BOOK);
        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rv_see_all_books.setLayoutManager(layoutManager);
        rv_see_all_books.setAdapter(seeAllBooksAdapter);

        if(type == C.TYPE_FOR_YOU | type == C.TYPE_BESTSELLER) {
            swipeRefreshLayout.setEnabled(false);
            return;
        }

        //Pagination options
        pagenumber = 1;
        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookList.clear();
                pagenumber = 1;
                loading = true;
                seeAllBooksAdapter.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadRecyclerViewData(type,langID, pagenumber, query);
                }else {
                    //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if(isInternetAvailable()) {
            loadRecyclerViewData(type, langID, pagenumber, query);
        }else {
            //Toast.makeText(SeeAllBooksActivity.this, getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
        }

        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        rv_see_all_books.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                                loadRecyclerViewData(type,langID, pagenumber, query);
                            }else {
                                //Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

    }

    private boolean isInternetAvailable() {
        return true;
    }

    //Load recyclerview data for new books
    private void loadRecyclerViewData(int type, final int lang_id, final int page, String query) {

        String baseUrl = C.BASE_URL;
        String apiUrl = "";
        switch(type) {
            case 6 : // new books
                apiUrl = String.format("/api/search-books-by-name?languageId=%1$s&page=%2$s&name=%3$s",
                        lang_id, page, query);
                break;

            case 0: //for free books
                apiUrl = String.format("/api/free-books?languageId=%1$s&page=%2$s",
                        lang_id, page);
                break;

            case 11 : // for collections
                apiUrl = String.format("/api/search-books-by-collection?languageId=%1$s&page=%2$s&collectionId=%3$s"
                , lang_id, page, collID);
                break;

            case 12: // for categories
                apiUrl = String.format("/api/search-books-by-category?languageId=%1$s&page=%2%s&categoryId=%3$s"
                        , lang_id, page, categoryID);
                break;

            case 3: //for audio books
                apiUrl = String.format("/api/books-with-sound?languageId=%1$s&page=%2$s",
                        lang_id, page);
                break;

            case 21: //for free audio books
                apiUrl = String.format("/api/free-books-with-sound?languageId=%1$s&page=%2$s",
                        lang_id, page);
                break;


            case 19 : //for by author books
                apiUrl = String.format("/api/search-books-by-author?languageId=%1$s&page=%2$s&authorId=%3$s",
                        lang_id, page, authorId);
                break;

            case 101: //searched audio books

            case 5 : //for you

            case 4 : //best seller

                if(!bookList.isEmpty()) {
                    loading = true;
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getActivity(), "No load more data", Toast.LENGTH_LONG).show();
                }

                seeAllBooksAdapter.notifyDataSetChanged();
                return;
        }

        String url = baseUrl + apiUrl;


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        swipeRefreshLayout.setRefreshing(false);
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

                                bookList.add(book);
                            }

                            if(!bookList.isEmpty()) {
                                loading = true;
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(getActivity(), "No load more data", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        seeAllBooksAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity()).getmRequestQueue();
        requestQueue.add(stringRequest);
    }
}
