package com.hajma.apps.hajmabooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hajma.apps.hajmabooks.R;

public class HomeFragmentContainer extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.child_fragment_container, container, false);

        getChildFragmentManager().beginTransaction().add(R.id.frg_ctnr, new FragmentHome(), "home").commit();

        return view;
    }
}
