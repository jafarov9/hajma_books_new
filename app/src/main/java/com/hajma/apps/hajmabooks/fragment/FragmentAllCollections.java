package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
import com.hajma.apps.hajmabooks.adapter.CollectionsAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.CollectionApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAllCollections extends Fragment {

    private RecyclerView rv_all_collections;
    private CollectionsAdapter collectionsAdapter;
    private ArrayList<CollectionApiModel> collList;
    private ImageButton imageButtonBackFromCollections;
    private int langID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_all_collections, container, false);

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
        imageButtonBackFromCollections = view.findViewById(R.id.imageButtonBackFromCollections);

        imageButtonBackFromCollections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rv_all_collections = view.findViewById(R.id.rv_all_collections);
        rv_all_collections.setLayoutManager(new LinearLayoutManager(getActivity()));
        collList = new ArrayList<>();
        collectionsAdapter = new CollectionsAdapter(getActivity(), collList);
        rv_all_collections.setAdapter(collectionsAdapter);

        loadRecyclerViewData(langID);
        return view;
    }

    private void loadRecyclerViewData(final int langID) {

        String url = C.BASE_URL +
                String.format("/api/get-collection?languageId=%1$s",
                        langID);

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

                                collList.add(collection);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        collectionsAdapter.notifyDataSetChanged();

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
}
