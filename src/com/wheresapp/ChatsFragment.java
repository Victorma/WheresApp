package com.wheresapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatsFragment extends Fragment {

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.contacts_list_view,
            container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
}
