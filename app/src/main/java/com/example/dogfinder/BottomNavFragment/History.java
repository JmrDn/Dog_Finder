package com.example.dogfinder.BottomNavFragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dogfinder.Adapter.HistoryAdapter;
import com.example.dogfinder.DateAndTimeFormatUtils;
import com.example.dogfinder.Model.HistoryModel;
import com.example.dogfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class History extends Fragment {

    HistoryAdapter historyAdapter;
    ArrayList<HistoryModel> list;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    String userId;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history, container, false);

        initWidgets(view);
        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
        userId = FirebaseAuth.getInstance().getUid();
        list = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyAdapter);

        CollectionReference collectionReference = firebaseFirestore.collection("Users").document(userId)
                .collection("daily_history");

        Query query = collectionReference.orderBy("date", Query.Direction.DESCENDING);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            list.clear();

                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                if(documentSnapshot.exists()){
                                    long heartRateInt = (long) documentSnapshot.get("bpmAverage");
                                    String heartRate = String.valueOf(heartRateInt);
                                    String date = DateAndTimeFormatUtils.wordDateFormat(documentSnapshot.getString("date"));
                                    String dateId = documentSnapshot.getString("dateId");
                                    double latitude = (double) documentSnapshot.get("latitude");
                                    double longitude = (double) documentSnapshot.get("longitude");
                                    String address = getCompleteAddressString(latitude, longitude);

                                    list.add(new HistoryModel(heartRate, address, date, dateId, latitude, longitude));

                                }
                                if (historyAdapter != null){
                                    historyAdapter.notifyDataSetChanged();
                                }

                            }
                        }

                    }
                });

    }

    private void initWidgets(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
}