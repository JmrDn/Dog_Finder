package com.example.dogfinder.BottomNavFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dogfinder.Login;
import com.example.dogfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    TextView name, email;
    AppCompatButton logoutBtn;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstances) {

        View view = layoutInflater.inflate(R.layout.activity_profile, container, false);

        initWidgets(view);
        setUpOwnersName();

        logoutBtn.setOnClickListener(v->{
            logout();
        });
        return view;
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(getContext(), Login.class));
    }

    private void setUpOwnersName() {

        String userID = firebaseAuth.getUid();

        firebaseFirestore.collection("Users").document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){
                                String NAME = documentSnapshot.getString("name");
                                String EMAIL = documentSnapshot.getString("email");

                                name.setText(NAME);
                                email.setText(EMAIL);
                            }
                        }
                    }
                });
    }

    private void initWidgets(View view) {
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        name = view.findViewById(R.id.ownersName_Textview);
        email = view.findViewById(R.id.ownersEmail_Textview);
        logoutBtn = view.findViewById(R.id.logout_Button);

    }
}