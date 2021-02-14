package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.hajma.apps.hajmabooks.adapter.CategoryAdapter;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.model.CategoryApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentCategory extends Fragment {

    private RecyclerView rvCategories;
    private ArrayList<CategoryApiModel> categories;
    private CategoryAdapter categoryAdapter;
    private int langID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

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
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        rvCategories = view.findViewById(R.id.rv_categories);

        //setup recyclerview and load data
        setupRecyclerView();



        return view;
    }

    private void setupRecyclerView() {

        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setAdapter(categoryAdapter);
        loadRecyclerviewData(langID);
    }

    private void loadRecyclerviewData(int langID) {

        String url = C.BASE_URL +
                String.format("/api/get-category?languageId=%1$s",
                        langID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("success").getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                CategoryApiModel category = new CategoryApiModel();
                                //category.setHorizontal_large(j.getString("horizontal_large"));
                                //category.setHorizontal_small(j.getString("horizontal_small"));
                                category.setId(j.getInt("id"));
                                category.setName(j.getString("name"));
                                //category.setVertical(j.getString("vertical"));

                                categories.add(category);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        categoryAdapter.notifyDataSetChanged();

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
