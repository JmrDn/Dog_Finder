package com.example.dogfinder.BottomNavFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dogfinder.EditProfile;
import com.example.dogfinder.Login;
import com.example.dogfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Profile extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    TextView name, email;
    AppCompatButton logoutBtn;
    AppCompatButton editProfileBtn;
    public  Intent intent;
    StorageReference storageReference;
    ShapeableImageView dogImageview;
    public String userPPFileName;
    private TextView dogNameTV;
    private TextView dogBreedTV;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstances) {

        View view = layoutInflater.inflate(R.layout.activity_profile, container, false);

        initWidgets(view);
        setUpOwnersNameAndDogInfo();
        setProfilePicture();

        intent = new Intent(getContext(), EditProfile.class);
        intent.putExtra("fileName", userPPFileName);

        logoutBtn.setOnClickListener(v->{
            logout();
        });

        editProfileBtn.setOnClickListener(v->{
            startActivity(intent);
        });
        return view;
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(getContext(), Login.class));
    }

    private void setUpOwnersNameAndDogInfo() {

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
                                String USERID = documentSnapshot.getString("userID");
                                String dogName = documentSnapshot.getString("dog_name");
                                String dogBreed = documentSnapshot.getString("dog_breed");

                                if (dogName != null &&
                                dogBreed != null){
                                    dogNameTV.setText(dogName);
                                    dogBreedTV.setText(dogBreed);
                                }



                                name.setText(NAME);
                                email.setText(EMAIL);
                            }
                        }
                    }
                });
    }

    private void setProfilePicture() {

        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()){
                            String fileName = documentSnapshot.getString("profilePicture");
                            userPPFileName = fileName;

                            retrieveImage(fileName);
                        }
                    }
                });
    }

    private void retrieveImage(String fileName){

        storageReference = FirebaseStorage.getInstance().getReference("images/usersProfilePic/"+ fileName);

        try {
            File localFile = File.createTempFile("tempfile", "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            dogImageview.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Failed to retrieve Profile Picture: " + e.getCause());

                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void initWidgets(View view) {
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        name = view.findViewById(R.id.ownersName_Textview);
        email = view.findViewById(R.id.ownersEmail_Textview);
        logoutBtn = view.findViewById(R.id.logout_Button);
        editProfileBtn = view.findViewById(R.id.editProfile_Button);
        dogImageview = view.findViewById(R.id.dog_Imageview);

        dogBreedTV = view.findViewById(R.id.dogBreed_Textview);
        dogNameTV = view.findViewById(R.id.dogName_Textview);

    }
}