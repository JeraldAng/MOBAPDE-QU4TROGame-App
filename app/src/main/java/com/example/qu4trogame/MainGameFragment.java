package com.example.qu4trogame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MainGameFragment extends Fragment {

    public MainGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the list_item_game layout
        View rootView = inflater.inflate(R.layout.list_item_game, container, false);

        return rootView;
    }
}
