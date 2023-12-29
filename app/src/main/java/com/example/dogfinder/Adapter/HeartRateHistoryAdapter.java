package com.example.dogfinder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogfinder.Model.HeartRateHistoryModel;
import com.example.dogfinder.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HeartRateHistoryAdapter extends RecyclerView.Adapter<HeartRateHistoryAdapter.MyViewHolder> {
    Context context;
    ArrayList<HeartRateHistoryModel> list;

    public HeartRateHistoryAdapter(Context context, ArrayList<HeartRateHistoryModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public HeartRateHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.heart_rate_history_list, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeartRateHistoryAdapter.MyViewHolder holder, int position) {
        HeartRateHistoryModel heartRateHistoryModel = list.get(position);
        holder.heartRate.setText(heartRateHistoryModel.getHeartRate());
        holder.time.setText(heartRateHistoryModel.getTime());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView heartRate, time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            heartRate = itemView.findViewById(R.id.heartRate_Textview);
            time = itemView.findViewById(R.id.time_Textview);
        }
    }
}
