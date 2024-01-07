package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dogfinder.BottomNavFragment.Home;
import com.example.dogfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ConnectToSensor extends AppCompatActivity {
    private Toolbar toolbar;
    private AppCompatEditText sensorIdET;
    private AppCompatButton connectBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_sensor);
        initWidgets();
        setUpToolbar();

        progressBar.setVisibility(View.GONE);
        connectBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            connectBtn.setVisibility(View.GONE);

            String sensorIdInput = sensorIdET.getText().toString();

            if(sensorIdInput.isEmpty()){
                progressBar.setVisibility(View.GONE);
                connectBtn.setVisibility(View.VISIBLE);
                sensorIdET.setError("Enter sensor ID");
            }
            else{
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(sensorIdInput).child("device_status");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            boolean isConnected = (boolean) snapshot.child("isConnected").getValue();
                            if(isConnected){
                                progressBar.setVisibility(View.GONE);
                                connectBtn.setVisibility(View.VISIBLE);
                                sensorIdET.setError("Sensor is already connected to other account");
                                sensorIdET.setText("");
                            }
                            else{


                                sensorIdET.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        connectToSensor(sensorIdInput);
                                       startActivity(new Intent(getApplicationContext(), HomePage.class));
                                        Toast.makeText(ConnectToSensor.this, "Successfully Connected to " + sensorIdInput, Toast.LENGTH_LONG).show();
                                        finishAffinity();

                                    }
                                },3000);


                            }
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            connectBtn.setVisibility(View.VISIBLE);

                            sensorIdET.setError("Enter valid sensor ID");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });
    }

    private void connectToSensor(String sensorId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(sensorId).child("device_status");

        databaseReference.child("isConnected").setValue(true);
        databaseReference.child("isConnectedTo").setValue(FirebaseAuth.getInstance().getUid());

        HashMap<String, Object> update = new HashMap<>();
        update.put("isConnected", true);
        update.put("isConnectedToSensorId", sensorId);

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                .update(update)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("TAG", "User connected to sensor ID " + sensorId);
                        }
                        else{
                            Log.d("TAG", "User failed to connect on " + sensorId);
                        }
                    }
                });



    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeActionContentDescription("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        sensorIdET = findViewById(R.id.sensorId_Edittext);
        connectBtn = findViewById(R.id.connect_Button);
        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}