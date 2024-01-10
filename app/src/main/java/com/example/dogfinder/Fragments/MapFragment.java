package com.example.dogfinder.Fragments;

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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dogfinder.ConnectToSensor;
import com.example.dogfinder.R;
import com.example.dogfinder.SensorInfo;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MapFragment extends Fragment implements OnMapReadyCallback {



    GoogleMap gMap;
    FrameLayout locatingLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Marker marker;
    private AppCompatButton directionBtn;
    private ImageView dogFocusLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        SupportMapFragment supportMapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this::onMapReady);
        initWidgets(view);
        locatingLayout.setVisibility(View.GONE);
        directionBtn.setVisibility(View.GONE);
        dogFocusLocation.setVisibility(View.GONE);

        mapInitialize();
        return  view;

    }

    private void initWidgets(View view) {
        locatingLayout = view.findViewById(R.id.locating_FrameLayout);
        directionBtn = view.findViewById(R.id.direction_Button);
        dogFocusLocation =view.findViewById(R.id.focusDogLocation_Imageview);

    }


    private void mapInitialize() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if(ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            return;
                        }

                        gMap.setMyLocationEnabled(true);
                        gMap.getUiSettings().setCompassEnabled(true);



                        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    if (documentSnapshot.exists()){
                                                        if (documentSnapshot.contains("isConnected") &&
                                                                documentSnapshot.contains("isConnectedToSensorId")){
                                                            boolean isConnected = documentSnapshot.getBoolean("isConnected");
                                                            String sensorId = documentSnapshot.getString("isConnectedToSensorId");

                                                            if(isConnected){
                                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(sensorId).child("location");
                                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                        if(snapshot.exists()){
                                                                            //Getting value from firebase real time database

                                                                            String latitudeString =  snapshot.child("latitude").getValue().toString();
                                                                            String longitudeString =  snapshot.child("longitude").getValue().toString();

                                                                            //latitude and longitude is null
                                                                            if (latitudeString.equals("0") && longitudeString.equals("0")) {

                                                                                locatingLayout.setVisibility(View.VISIBLE);
                                                                            }
                                                                            // latitude and longitude is not null
                                                                            else{
                                                                                locatingLayout.setVisibility(View.GONE);
                                                                                directionBtn.setVisibility(View.VISIBLE);
                                                                                dogFocusLocation.setVisibility(View.VISIBLE);



                                                                                directionBtn.setOnClickListener(v->{
                                                                                    String from = location.getLatitude() + "," + location.getLongitude();
                                                                                    String to = latitudeString + "," + longitudeString;

                                                                                    getDirections(from, to);
                                                                                });
                                                                                double latitude = Double.parseDouble(latitudeString);
                                                                                double longitude = Double.parseDouble(longitudeString);
                                                                                if (marker != null) {
                                                                                    marker.remove();
                                                                                }

                                                                                LatLng latLng = new LatLng(latitude, longitude);
                                                                                MarkerOptions markerOptions = new MarkerOptions();
                                                                                markerOptions.title("Dog's location");
                                                                                if (getContext()!= null){
                                                                                    markerOptions.position(latLng).icon(setIcon((Activity) getContext(), R.drawable.dog_mark_location_icon));

                                                                                    marker = gMap.addMarker(markerOptions);

                                                                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);

                                                                                    if (!gMap.getUiSettings().isZoomControlsEnabled())
                                                                                        gMap.animateCamera(cameraUpdate);

                                                                                    dogFocusLocation.setOnClickListener(v->{
                                                                                        gMap.animateCamera(cameraUpdate);
                                                                                    });
                                                                                }

                                                                            }


                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                        Log.d("TAG", "Failed to retrieve location");

                                                                    }
                                                                });
                                                            }

                                                        }

                                                    }
                                                }
                                            }
                                        });





                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "Permission " + permissionDeniedResponse.getPermissionName() + " was denied", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}