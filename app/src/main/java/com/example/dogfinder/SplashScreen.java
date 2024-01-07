package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()){
                                            if (!documentSnapshot.contains("dog_name") ||
                                                    !documentSnapshot.contains("dog_breed")){
                                                startActivity(new Intent(getApplicationContext(), NewUserPage1.class));
                                            }
                                            else{
                                                startActivity(new Intent(getApplicationContext(), Login.class));
                                            }
                                        }
                                    }
                                }
                            });

                }

            }
        }, 4000);
    }


}