package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SensorInfo extends AppCompatActivity {
    private TextView sensorIdTV;
    private AppCompatButton disconnectBtn;
    private ImageView connectIV;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);
        initWidgets();
        setUpToolbar();

        connectIV.setColorFilter(ContextCompat.getColor(this, R.color.color_primary));

        Intent intent = getIntent();
        String sensorId = intent.getStringExtra("sensorId");
        boolean isConnected = intent.getBooleanExtra("isConnected", false);

        if (isConnected){
            disconnectBtn.setOnClickListener(v->{




                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(sensorId).child("device_status");

                        databaseReference.child("isConnected").setValue(false);
                        databaseReference.child("isConnectedTo").setValue(null);
                        HashMap<String, Object> update = new HashMap<>();
                        update.put("isConnected", false);
                        update.put("isConnectedToSensorId", null);

                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                                .update(update)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Failed to disconnect", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                        Toast.makeText(getApplicationContext(), "Successfully disconnected", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        finish();


                    }
                }, 1500);
            });

        }

        sensorIdTV.setText(sensorId);




    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeActionContentDescription("Back");
    }

    private void initWidgets() {

        sensorIdTV = findViewById(R.id.sensorId_Textview);
        disconnectBtn = findViewById(R.id.disconnect_Button);
        connectIV = findViewById(R.id.connect_Imageview);

        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}