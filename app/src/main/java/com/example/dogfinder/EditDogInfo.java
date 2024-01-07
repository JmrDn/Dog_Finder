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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditDogInfo extends AppCompatActivity {
    private AppCompatEditText newDogNameET;
    private AppCompatEditText newDogBreedET;
    private AppCompatButton saveBtn;
    private AppCompatButton cancelBtn;
    private TextView dogBreedTV;
    private TextView dogNameTV;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dog_info);
        initWidgets();
        setUpDogInfo();
        setUpToolbar();

        saveBtn.setEnabled(false);
        getTheEdittext();

        saveBtn.setOnClickListener(v->{

            String dogName = newDogNameET.getText().toString();
            String dogBreed = newDogBreedET.getText().toString();

            HashMap<String, Object> update = new HashMap<>();

            if(dogName != null && dogBreed.isEmpty()){
                update.put("dog_name", dogName);
            }
            else if(dogBreed != null && dogName.isEmpty()){
                update.put("dog_breed", dogBreed);
            }
            else{
                update.put("dog_breed", dogBreed);
                update.put("dog_name", dogName);
            }



            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                    .update(update)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Successfully changed", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                intent.putExtra("value", "profile");
                                startActivity(intent);
                            }
                            else{
                                Log.d("TAG", "Dog name and breed failed to update");
                            }
                        }
                    });


        });
        cancelBtn.setOnClickListener(v->{
            onBackPressed();
        });

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeActionContentDescription("Back");
    }

    private void setUpDogInfo() {

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){

                                String dogName = documentSnapshot.getString("dog_name");
                                String dogBreed = documentSnapshot.getString("dog_breed");

                                if (dogName != null &&
                                        dogBreed != null){
                                    dogNameTV.setText(dogName);
                                    dogBreedTV.setText(dogBreed);
                                }




                            }
                        }
                    }
                });
    }

    private void getTheEdittext() {
        String dogName = newDogNameET.getText().toString();
        String dogBreed = newDogBreedET.getText().toString();

        if (!dogBreed.isEmpty() || !dogName.isEmpty()){
            saveBtn.setEnabled(true);
        }

        refreshGetTheEdittext(1000);
    }

    private void refreshGetTheEdittext(int i) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTheEdittext();;
            }
        }, i);
    }

    private void initWidgets() {
        saveBtn = findViewById(R.id.save_Button);
        cancelBtn = findViewById(R.id.cancel_Button);
        newDogBreedET = findViewById(R.id.newDogBreed_Edittext);
        newDogNameET = findViewById(R.id.newDogName_Edittext);

        dogBreedTV = findViewById(R.id.dogBreed_Textview);
        dogNameTV = findViewById(R.id.dogName_Textview);

        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}