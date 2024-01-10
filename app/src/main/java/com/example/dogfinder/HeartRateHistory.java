package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.dogfinder.Adapter.HeartRateHistoryAdapter;
import com.example.dogfinder.Model.HeartRateHistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HeartRateHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HeartRateHistoryAdapter myAdapter;
    private ArrayList<HeartRateHistoryModel> list;
    private TextView date;
    private String dateId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_history);
        initWidgets();
        setUpGetIntent();
        setUpRecyclerview();
        setUpToolbar();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeActionContentDescription("Back");
    }

    private void setUpGetIntent() {
        Intent intent = getIntent();
        date.setText(intent.getStringExtra("date"));
        dateId = intent.getStringExtra("dateId");

    }

    private void setUpRecyclerview() {
        String userId = FirebaseAuth.getInstance().getUid();
        list = new ArrayList<>();
        myAdapter = new HeartRateHistoryAdapter(getApplicationContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(myAdapter);

        CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users")
                .document(userId).collection("daily_history")
                .document(dateId).collection(dateId);

        if (collectionReference != null){
            Query query = collectionReference.orderBy("date_and_time", Query.Direction.DESCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot querySnapshot = task.getResult();

                                if (!querySnapshot.isEmpty() && querySnapshot != null){
                                    list.clear();
                                    String previousTime = "";
                                    for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                        String heartRate = documentSnapshot.getString("heart_rate");
                                        String dateAndTime = documentSnapshot.getString("date_and_time");
                                        String time = DateAndTimeFormatUtils.dateAndTimeConvertedToTimeFormat(dateAndTime);
                                        if (!previousTime.equals(time)){
                                            list.add(new HeartRateHistoryModel(heartRate, time));
                                            previousTime = time;
                                        }




                                    }
                                    if(myAdapter != null){
                                        myAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                        }
                    });
        }


    }

    private void initWidgets() {
        date = findViewById(R.id.date_Textview);
        recyclerView =findViewById(R.id.heartRateHistory_Recyclerview);

        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}