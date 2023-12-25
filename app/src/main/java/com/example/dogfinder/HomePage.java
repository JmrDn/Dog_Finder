package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.dogfinder.BottomNavFragment.History;
import com.example.dogfinder.BottomNavFragment.Home;
import com.example.dogfinder.BottomNavFragment.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomePage extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    public boolean isHomeFragmentActive;

    public String value;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        Intent intent = getIntent();
        value = intent.getStringExtra("value");

        if (value == null){
            value = "Home";
        }
        else{
            bottomNavigationView.setSelectedItemId(R.id.Profile);

        }


        bottomNavigationView.setOnItemSelectedListener(this);

        Fragment selectedFragment = null;

        if (value.equals("Home")){
            isHomeFragmentActive = true;
            selectedFragment = new Home();
        }
        else if (value.equals("profile")){
            isHomeFragmentActive = false;
            selectedFragment = new Profile();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.Home){
            isHomeFragmentActive = true;
            selectedFragment = new Home();

        }
        else if (itemId == R.id.History){
            isHomeFragmentActive = false;
            selectedFragment = new History();
        }
        else if (itemId == R.id.Profile){
            isHomeFragmentActive = false;
            selectedFragment = new Profile();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
        return true;
    }

    @Override
    public void onBackPressed() {

        if(isHomeFragmentActive){
            finishAffinity();
        }
        else{
            super.onBackPressed();
        }

    }
}