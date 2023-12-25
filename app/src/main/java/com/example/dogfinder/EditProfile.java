package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private AppCompatButton cancelBtn, uploadImageBtn;
    private ShapeableImageView dogImage;
    private ImageView selectImageBtn;
    private StorageReference storageReference;
    LinearLayout buttonLayout;
    Toolbar toolbar;
    Uri uri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initWidgets();

        setUpToolbar();
        setProfilePicture();
        buttonLayout.setVisibility(View.GONE);
        
        cancelBtn.setOnClickListener(v->{
            onBackPressed();
        });
        
        uploadImageBtn.setOnClickListener(v->{
            uploadImage();
        });
        

        selectImageBtn.setOnClickListener(v->{
            ImagePicker.with(EditProfile.this)
                    .crop(1,1)	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();

        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeActionContentDescription("Back");
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
                            dogImage.setImageBitmap(bitmap);

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

    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading new Profile Picture");
        progressDialog.show();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhm");
        Date date = new Date();
        String currentDate = simpleDateFormat.format(date);
        String userId = FirebaseAuth.getInstance().getUid();
        String fileName = userId + currentDate;

        storageReference = FirebaseStorage.getInstance().getReference("images/usersProfilePic/"+fileName);
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dogImage.setImageURI(null);
                        if (progressDialog.isShowing()){

                            HashMap<String, Object> updateProfilePic = new HashMap<>();
                            updateProfilePic.put("profilePicture", fileName);

                            FirebaseFirestore.getInstance().collection("Users").document(userId)
                                    .update(updateProfilePic)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            intent.putExtra("value", "profile");
                            startActivity(intent);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    private void initWidgets() {
        cancelBtn = findViewById(R.id.cancel_Button);
        uploadImageBtn = findViewById(R.id.save_Button);
        dogImage = findViewById(R.id.dog_Imageview);
        selectImageBtn = findViewById(R.id.uploadImage_Imageview);
        buttonLayout = findViewById(R.id.button_LinearLayout);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            buttonLayout.setVisibility(View.VISIBLE);
            uri = data.getData();
            dogImage.setImageURI(uri);
        }
        else if(resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(getApplicationContext(), ImagePicker.getError(data), Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(getApplicationContext(), "Task cancelled", Toast.LENGTH_LONG).show();
        }
    }
}