package com.example.dogfinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogfinder.DayActivity;
import com.example.dogfinder.Model.HistoryModel;
import com.example.dogfinder.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    Context context;
    ArrayList<HistoryModel> list;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        HistoryModel historyModel = list.get(position);
        holder.heartRate.setText(historyModel.getHeartRate());
        holder.date.setText(historyModel.getDate());
        holder.address.setText(historyModel.getAddress());
        holder.latitudeString = String.valueOf(historyModel.getLatitude());
        holder.longitudeString = String.valueOf(historyModel.getLongitude());

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, DayActivity.class);
            intent.putExtra("dateId", historyModel.getDateId());
            intent.putExtra("date", historyModel.getDate());
            intent.putExtra("latitude", holder.latitudeString);
            intent.putExtra("longitude", holder.longitudeString);
            intent.putExtra("heartRate", historyModel.getHeartRate());
            intent.putExtra("highestHeartRate", historyModel.getHighestHeartRate());
            intent.putExtra("lowestHeartRate", historyModel.getLowestHeartRate());
            intent.putExtra("averageHeartRate",historyModel.getAverageHeartRate());


            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView address, date, heartRate;
        String latitudeString, longitudeString;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.address_Textview);
            date = itemView.findViewById(R.id.date_Textview);
            heartRate = itemView.findViewById(R.id.heartRate_Textview);
        }
    }
}
