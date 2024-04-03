package com.example.cleanbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShortsFragment extends Fragment {

    public ShortsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shorts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the FloatingActionButton
        FloatingActionButton fabScanHistory = view.findViewById(R.id.floatingActionButton1);

        // Set OnClickListener to FloatingActionButton
        fabScanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to ScanHistoryActivity
                Intent intent = new Intent(getActivity(), ScanHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
