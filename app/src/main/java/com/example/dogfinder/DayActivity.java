package com.example.dogfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class DayActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView toolbarTitle,
            highestHeartRate,
            lowestHeartRate,
            noLocation,
            averageHeartRate;
    private String highestHeartRateString,
            lowestHeartRateString,
            averageHeartRateString,
            date,
            dateId;
    private double latitude,
                    longitude;

    private LineChart heartRateLineChart;
    private ArrayList<Entry> entry;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private ImageView getDirectionBtn;
    private FrameLayout map_FrameLayout;
    private Toolbar toolbar;
    private AppCompatButton historyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        initWidgets();

        SupportMapFragment supportMapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MY_MAP_HISTORY);
        supportMapFragment.getMapAsync(this::onMapReady);

        setUpToolbar();
        setUpGetIntent();
        setUpHighestAndLowestHeartRate();
        setUpLineChart();
        mapInitialize();

        noLocation.setVisibility(View.INVISIBLE);

        historyBtn.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), HeartRateHistory.class);
            intent.putExtra("date", date);
            intent.putExtra("dateId", dateId);
            startActivity(intent);
        });


    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeActionContentDescription("Back");
    }

    private void mapInitialize() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    private void setUpLineChart() {
        String userId = FirebaseAuth.getInstance().getUid();
        entry = new ArrayList<>();

        CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users")
                .document(userId).collection("daily_history")
                .document(dateId).collection(dateId);


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



        if (collectionReference != null) {
            Query query = collectionReference.orderBy("date_and_time", Query.Direction.ASCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();

                                if (!querySnapshot.isEmpty() && querySnapshot != null) {
                                    int num = 0;
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        String heartRateString = documentSnapshot.getString("heart_rate");
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
                                } else {
                                    Log.d("TAG", "no such query snapshot for fetching data history of gps and heart rate");
                                }
                            }
                        }
                    });
        }
    }

    private void setUpGetIntent() {
        Intent intent = getIntent();
        highestHeartRateString = intent.getStringExtra("highestHeartRate");
        lowestHeartRateString = intent.getStringExtra("lowestHeartRate");
        averageHeartRateString = intent.getStringExtra("averageHeartRate");

        String latitudeString = intent.getStringExtra("latitude");
        String longitudeString = intent.getStringExtra("longitude");
        latitude = Double.parseDouble(latitudeString);
        longitude = Double.parseDouble(longitudeString);

        date = intent.getStringExtra("date");
        dateId = intent.getStringExtra("dateId");


    }

    private void setUpHighestAndLowestHeartRate() {

        highestHeartRate.setText(highestHeartRateString);
        lowestHeartRate.setText(lowestHeartRateString);
        averageHeartRate.setText(averageHeartRateString);

        toolbarTitle.setText(date);
    }

    private void initWidgets() {
        toolbarTitle = findViewById(R.id.toolBarDate_Textview);
        highestHeartRate = findViewById(R.id.highestHeartRate_Textview);
        lowestHeartRate = findViewById(R.id.lowestHeartRate_Textview);
        averageHeartRate = findViewById(R.id.averageHeartRate_Textview);

        heartRateLineChart = findViewById(R.id.heartRate_LineChart);

        getDirectionBtn = findViewById(R.id.getDirection_Imageview);

        noLocation = findViewById(R.id.noLocation_Textview);
        map_FrameLayout = findViewById(R.id.map_FrameLayout);

        toolbar = findViewById(R.id.toolbar);

        historyBtn = findViewById(R.id.heartRateHistory_Button);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;

        Dexter.withContext(getApplicationContext())
                .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            return;
                        }

                        gMap.setMyLocationEnabled(true);
                        gMap.getUiSettings().setCompassEnabled(true);

                        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                String latitudeString =  String.valueOf(latitude);
                                String longitudeString = String.valueOf(longitude);

                                //latitude and longitude is null
                                if (latitude == 0 && longitude == 0) {

                                    noLocation.setVisibility(View.VISIBLE);
                                    map_FrameLayout.setVisibility(View.GONE);
                                    getDirectionBtn.setVisibility(View.GONE);
                                }
                                // latitude and longitude is not null
                                else{
                                    noLocation.setVisibility(View.GONE);
                                    getDirectionBtn.setVisibility(View.VISIBLE);
                                    map_FrameLayout.setVisibility(View.VISIBLE);

                                    getDirectionBtn.setOnClickListener(v->{
                                        String from = location.getLatitude() + "," + location.getLongitude();
                                        String to = latitudeString + "," + longitudeString;

                                        getDirections(from, to);
                                    });


                                    if (marker != null) {
                                        marker.remove();
                                    }

                                    if (getApplicationContext()!= null){
                                        LatLng latLng = new LatLng(latitude, longitude);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.title("Dog's location");
                                        markerOptions.position(latLng).icon(setIcon((Activity) DayActivity.this, R.drawable.dog_mark_location_icon));

                                        marker = gMap.addMarker(markerOptions);

                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                                        gMap.animateCamera(cameraUpdate);
                                    }

                                }

                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Permission " + permissionDeniedResponse.getPermissionName() + " was denied", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    public BitmapDescriptor setIcon (Context context, int drawableId){
        Drawable drawable =ActivityCompat.getDrawable(context, drawableId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas =new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void getDirections(String from, String to){
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + from + "/" + to);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}