package com.example.dogfinder.BottomNavFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.dogfinder.DogLiveLocation;
import com.example.dogfinder.R;


public class Home extends Fragment {

    CardView dogLocationBtn;

    ProgressBar heartRateProgress;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        dogLocationBtn = view.findViewById(R.id.dogLocation_Cardview);
        heartRateProgress = view.findViewById(R.id.heartRate_Progressbar);

        heartRateProgress.setMax(250);
        heartRateProgress.setProgress(120);

        dogLocationBtn.setOnClickListener(v->{
            startActivity(new Intent(getContext(), DogLiveLocation.class));
        });


        return view;
    }
}