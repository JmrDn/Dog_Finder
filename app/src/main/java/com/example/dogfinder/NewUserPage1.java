package com.example.dogfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;

public class NewUserPage1 extends AppCompatActivity {
    private AppCompatEditText dogNameET;
    private AppCompatButton nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_page1);
        initWidgets();

        nextBtn.setOnClickListener(v->{
            String dogName = dogNameET.getText().toString();

            if (dogName.isEmpty()){
                dogNameET.setError("Enter dog name");
            }
            else{
                Intent intent = new Intent(getApplicationContext(), NewUserPage2.class);
                intent.putExtra("dogName", dogName);
                startActivity(intent);
            }
        });
    }

    private void initWidgets() {
        dogNameET = findViewById(R.id.dogName_Edittext);
        nextBtn = findViewById(R.id.next_Button);
    }
}