package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;

public class NewUserPage2 extends AppCompatActivity {
    private AppCompatEditText dogBreedET;
    private AppCompatButton doneBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_page2);
        initWidgets();


        progressBar.setVisibility(View.GONE);
        doneBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            doneBtn.setVisibility(View.GONE);

            Intent intent = getIntent();
            String dogName = intent.getStringExtra("dogName");
            String dogBreed = dogBreedET.getText().toString();
            String userId = FirebaseAuth.getInstance().getUid();

            if(dogBreed.isEmpty()){
                progressBar.setVisibility(View.GONE);
                doneBtn.setVisibility(View.VISIBLE);
                dogBreedET.setError("Enter dog breed");
            }
            else{

                HashMap<String, Object> update = new HashMap<>();
                update.put("dog_name", dogName);
                update.put("dog_breed", dogBreed);
                FirebaseFirestore.getInstance().collection("Users").document(userId)
                        .update(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                                        }
                                    }, 3000);
                                }
                            }
                        });

            }



        });
    }

    private void initWidgets() {
        dogBreedET = findViewById(R.id.dogBreed_Edittext);
        doneBtn = findViewById(R.id.done_Button);
        progressBar = findViewById(R.id.progressbar);
    }
}