package com.example.dogfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.dogfinder.Fragments.MapFragment;

public class DogLiveLocation extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_live_location);




        setUpMapFragment();
        setUpToolbar();
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeActionContentDescription("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUpMapFragment() {
        Fragment fragment = new MapFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}