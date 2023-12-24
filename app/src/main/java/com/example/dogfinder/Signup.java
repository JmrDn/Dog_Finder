package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
    TextView alreadyHaveAccount;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    AppCompatEditText email, name, password, confirmPassword;
    AppCompatButton signupBtn;
    ProgressBar progressBar;
    boolean passwordVisible, confirmPasswordVisible;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initWidgets();
        passwordHideMethod();
        alreadyHaveAccount.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), Login.class));
        });

        signupBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setVisibility(View.GONE);

            String EMAIL = email.getText().toString();
            String NAME = name.getText().toString();
            String PASSWORD = password.getText().toString();
            String CONFIRM_PASSWORD = confirmPassword.getText().toString();

            if(EMAIL.isEmpty()){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                email.setError("Enter email");
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                email.setError("Enter valid email");
            }
            else if(NAME.isEmpty()){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                name.setError("Enter name");
            }
            else if(PASSWORD.isEmpty()){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                password.setError("Enter password");
            }
            else if(PASSWORD.length() < 8){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                password.setError("Password must have at least 8 characters");
            }
            else if(CONFIRM_PASSWORD.isEmpty()){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                confirmPassword.setError("Enter password");
            }
            else if(!PASSWORD.equals(CONFIRM_PASSWORD)){
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);

                password.setError("Password not match");
                confirmPassword.setError("Password not match");
            }
            else{

                progressBar.setVisibility(View.VISIBLE);
                signupBtn.setVisibility(View.GONE);

                registerUser(EMAIL, NAME, CONFIRM_PASSWORD);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void passwordHideMethod() {
        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = password.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {

                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        confirmPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= confirmPassword.getRight()-confirmPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = confirmPassword.getSelectionEnd();
                        if (confirmPasswordVisible){
                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPasswordVisible = false;
                        }
                        else {

                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPasswordVisible = true;

                        }
                        confirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void registerUser(String email, String name, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.VISIBLE);
                            signupBtn.setVisibility(View.GONE);
                            //Successfully sign up
                            saveUserLogin(email, name);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            signupBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Sign up failed, Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserLogin(String email, String name) {
        String userID = firebaseAuth.getUid();

        HashMap<String ,Object> userDetails = new HashMap<>();
        userDetails.put("email", email);
        userDetails.put("name", name);
        userDetails.put("userID", userID);

        firebaseFirestore.collection("Users").document(userID)
                .set(userDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //User details saved successfully

                            Toast.makeText(getApplicationContext(), "Successfully Register", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                        else{
                            Log.d("TAG", "Firebase error: User details not save");
                        }
                    }
                });
    }

    private void initWidgets() {
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount_Textview);

        email = findViewById(R.id.email_Edittext);
        name = findViewById(R.id.name_Edittext);
        password = findViewById(R.id.password_Edittext);
        confirmPassword = findViewById(R.id.confirmPassword_Edittext);
        signupBtn = findViewById(R.id.signup_Button);
        progressBar = findViewById(R.id.signup_ProgressBar);

        //Firebase database
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }
}