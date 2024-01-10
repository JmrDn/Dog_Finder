package com.example.dogfinder.BottomNavFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogfinder.ConnectToSensor;
import com.example.dogfinder.DateAndTimeFormatUtils;
import com.example.dogfinder.DogLiveLocation;
import com.example.dogfinder.R;
import com.example.dogfinder.SensorInfo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Home extends Fragment {

    CardView dogLocationBtn;

    ProgressBar heartRateProgress;
    LineChart heartRateLineChart;
    ArrayList<Entry> entry;
    FirebaseFirestore firebaseFirestore;
    int num = 1;
    public String userId;
    public  CollectionReference collectionReference;
    TextView heartRateTV, timeHeartRateTV;
    double latitude = 0;
    double longitude = 0;
    private ImageView connectToSensorBtn;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        initWidgets(view);
        setUpUserConnectionToSensor();
        setUpLineChart();
        getDataAverage();




        dogLocationBtn.setOnClickListener(v->{
            startActivity(new Intent(getContext(), DogLiveLocation.class));
        });

        return view;
    }



    private void setUpUserConnectionToSensor() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (getContext()!= null){
                                if (documentSnapshot.exists()){
                                    if (documentSnapshot.contains("isConnected") &&
                                            documentSnapshot.contains("isConnectedToSensorId")){
                                        boolean isConnected = documentSnapshot.getBoolean("isConnected");
                                        String sensorId = documentSnapshot.getString("isConnectedToSensorId");

                                        if(isConnected){

                                            setUpHeartRateProgress(sensorId);
                                            setTodayHeartRateAndLocation(sensorId);
                                            connectToSensorBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_primary));
                                            connectToSensorBtn.setOnClickListener(v->{
                                                Intent intent = new Intent(getContext(), SensorInfo.class);
                                                intent.putExtra("sensorId", sensorId);
                                                intent.putExtra("isConnected", true);
                                                startActivity(intent);
                                            });
                                        }
                                        else{
                                            heartRateTV.setText("0");
                                            heartRateProgress.setBackgroundResource(R.drawable.low_heart_rate_progress_bg);
                                            heartRateProgress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.low_heart_rate_progress_bg));
                                            connectToSensorBtn.setColorFilter(Color.RED);

                                            connectToSensorBtn.setOnClickListener(v->{
                                                startActivity(new Intent(getContext(), ConnectToSensor.class));
                                            });
                                        }
                                    }
                                    else{
                                        heartRateTV.setText("0");
                                        heartRateProgress.setBackgroundResource(R.drawable.low_heart_rate_progress_bg);
                                        heartRateProgress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.low_heart_rate_progress_bg));
                                        connectToSensorBtn.setColorFilter(Color.RED);

                                        connectToSensorBtn.setOnClickListener(v->{
                                            startActivity(new Intent(getContext(), ConnectToSensor.class));
                                        });
                                    }
                                }
                            }
                            }
                        }
                });
    }

    private void setUpHeartRateProgress(String sensorId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(sensorId).child("heart_rate");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Date date = new Date();
                    String heartRateString = snapshot.child("bpm").getValue().toString();
                    int heartRate = Integer.parseInt(heartRateString);
                    String time = DateAndTimeFormatUtils.timeFormat(date);

                    heartRateTV.setText(heartRateString);
                    timeHeartRateTV.setText("As of " + time);

                    heartRateProgress.setMax(150);
                    heartRateProgress.setProgress(heartRate);


                if (getContext()!= null){
                    //Set color of progressbar
                    if (heartRateProgress.getProgress() < 70 ){

                        //Low Heart rate
                        heartRateProgress.setBackgroundResource(R.drawable.low_heart_rate_progress_bg);
                        heartRateProgress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.low_heart_rate_progress_bg));

                    }
                    else if( heartRateProgress.getProgress() > 69 && heartRateProgress.getProgress() < 121){

                        //Normal Heart rate
                        heartRateProgress.setBackgroundResource(R.drawable.normal_heart_rate_progress_bg);
                        heartRateProgress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.normal_heart_rate_progress_bg));

                    }
                    else if (heartRateProgress.getProgress() > 120){

                        //High Heart rate
                        heartRateProgress.setBackgroundResource(R.drawable.heart_rate_progress_background);
                        heartRateProgress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.heart_rate_progress_background));


                    }
                }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Database error: " + error.getMessage());

            }
        });



    }

    private void setTodayHeartRateAndLocation(String sensorId) {



        //Get Location
        DatabaseReference databaseReferenceLocation = FirebaseDatabase.getInstance().getReference(sensorId).child("location");

        databaseReferenceLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()){
                    String latitudeString = snapshot.child("latitude").getValue().toString();
                    String longitudeString = snapshot.child("longitude").getValue().toString();

                    latitude = Double.parseDouble(latitudeString);
                    longitude = Double.parseDouble(longitudeString);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Database error: " + error.getMessage());
            }
        });



        //Get heart rate
        DatabaseReference databaseReferenceHeartRate = FirebaseDatabase.getInstance().getReference(sensorId).child("heart_rate");

        databaseReferenceHeartRate.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String heartRate = snapshot.child("bpm").getValue().toString();
                    Date currentDateAndTime = new Date();
                    String timeStamp = DateAndTimeFormatUtils.dateAndTime(currentDateAndTime);
                    String dateForDocumentName = DateAndTimeFormatUtils.dateForDocumentName(currentDateAndTime);

                    collectionReference =  firebaseFirestore.collection("Users")
                            .document(userId).collection("daily_history")
                            .document(dateForDocumentName).collection(dateForDocumentName);

                    HashMap<String, Object> todayHeartRateAndLocation = new HashMap<>();

                    if (Integer.parseInt(heartRate) != 0 && heartRate != null){
                        todayHeartRateAndLocation.put("heart_rate", heartRate);
                        todayHeartRateAndLocation.put("date_and_time", timeStamp);
                        todayHeartRateAndLocation.put("latitude", latitude);
                        todayHeartRateAndLocation.put("longitude", longitude);
                    }







                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            collectionReference.add(todayHeartRateAndLocation)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    },10000);




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage());

            }
        });


    }

    private void getDataAverage(){

        Date currentDateAndTime = new Date();
        String dateForDocumentName = DateAndTimeFormatUtils.dateForDocumentName(currentDateAndTime);
        collectionReference =  firebaseFirestore.collection("Users")
                .document(userId).collection("daily_history")
                .document(dateForDocumentName).collection(dateForDocumentName);


        if(collectionReference != null){

            Query query = collectionReference.orderBy("date_and_time", Query.Direction.DESCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful()){

                                QuerySnapshot queryDocumentSnapshots = task.getResult();

                                if (!queryDocumentSnapshots.isEmpty()){

                                    if(queryDocumentSnapshots.getDocuments().get(0).exists()){
                                        DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0);

                                        latitude = (double)latestDocument.get("latitude");
                                        longitude = (double) latestDocument.get("longitude");

                                    }
                                    else{
                                        latitude = 0;
                                        longitude = 0;
                                    }


                                    int heartRate;
                                    int totalHeartRate = 0;
                                    int heartRateAve = 0;
                                    int heartRateDataLength = 0;
                                    int highestHeartRate = 0;
                                    int lowestHeartRate = 400;

                                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                                        String heartRateString = documentSnapshot.getString("heart_rate");
                                        heartRate = Integer.parseInt(heartRateString);

                                        if(heartRate != 0){
                                            if (heartRate > highestHeartRate)
                                                highestHeartRate = heartRate;
                                            if (heartRate < lowestHeartRate)
                                                lowestHeartRate = heartRate;
                                        }

                                        totalHeartRate += heartRate;
                                        heartRateDataLength++;
                                    }

                                    if (totalHeartRate != 0 && heartRateDataLength != 0){
                                        heartRateAve = totalHeartRate / heartRateDataLength;
                                    }
                                    setDataAverage(heartRateAve, latitude, longitude, highestHeartRate, lowestHeartRate);

                                }


                            }

                        }
                    });
        }



    }

    private void setDataAverage(int heartRateAve, double latitude, double longitude, int highestHeartRate, int lowestHeartRate) {
        Date date = new Date();

        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document(userId).collection("daily_history")
                .document(DateAndTimeFormatUtils.dateForDocumentName(date));

        HashMap<String, Object> averageDocument = new HashMap<>();
        averageDocument.put("bpmAverage", heartRateAve);
        averageDocument.put("date", DateAndTimeFormatUtils.dateFormat(date));
        averageDocument.put("latitude", latitude);
        averageDocument.put("longitude", longitude);
        averageDocument.put("dateId", DateAndTimeFormatUtils.dateForDocumentName(date));
        averageDocument.put("highest_heart_rate", highestHeartRate);
        averageDocument.put("lowest_heart_rate", lowestHeartRate);

        if(documentReference != null){
            documentReference.set(averageDocument)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "Average set");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Average set failed");
                        }
                    });

        }






    }


    private void setUpLineChart() {
        entry = new ArrayList<>();
        heartRateLineChart.getAxisRight().setEnabled(false);


        XAxis xAxis = heartRateLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(-150f);
        xAxis.setGranularityEnabled(true);

        YAxis yAxis = heartRateLineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(1f);



        if (collectionReference != null){
            Query query = collectionReference.orderBy("date_and_time", Query.Direction.ASCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                QuerySnapshot querySnapshot = task.getResult();

                                if(!querySnapshot.isEmpty() && querySnapshot != null){
                                    int num = 0;
                                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        String heartRateString =  documentSnapshot.getString("heart_rate");
                                        int heartRate = Integer.parseInt(heartRateString);
                                        num++;
                                        entry.add(new Entry(num, heartRate));

                                    }
                                    LineDataSet lineDataSet = new LineDataSet(entry, "Heart Rate");
                                    lineDataSet.setDrawCircleHole(false);
                                    lineDataSet.setDrawCircles(false);
                                    lineDataSet.setColor(Color.RED);
                                    lineDataSet.setDrawValues(false);
                                    lineDataSet.setLineWidth(3);

                                    LineData data = new LineData(lineDataSet);
                                    heartRateLineChart.setData(data);
                                    heartRateLineChart.animateX(1);
                                }
                                else{
                                    Log.d("TAG", "no such query snapshot for fetching data history of gps and heart rate");
                                }
                            }
                        }
                    });

        }







    refreshLineChart(1000);
    }

    private void initWidgets(View view) {
        dogLocationBtn = view.findViewById(R.id.dogLocation_Cardview);
        heartRateProgress = view.findViewById(R.id.heartRate_Progressbar);
        heartRateLineChart = view.findViewById(R.id.heartRate_LineChart);
        timeHeartRateTV = view.findViewById(R.id.timeOfHeartRate_Textview);
        heartRateTV = view.findViewById(R.id.heartRate_Textview);

        firebaseFirestore = FirebaseFirestore.getInstance();

         userId = FirebaseAuth.getInstance().getUid();

         connectToSensorBtn = view.findViewById(R.id.connect_Imageview);
    }

    private void refreshLineChart(int milliseconds){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpLineChart();
            }
        },milliseconds);
    }


}