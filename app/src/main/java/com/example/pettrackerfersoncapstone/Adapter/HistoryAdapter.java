package com.example.pettrackerfersoncapstone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pettrackerfersoncapstone.Model.HistoryModel;
import com.example.pettrackerfersoncapstone.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    ArrayList<HistoryModel> list;
    Context context;

    public HistoryAdapter(ArrayList<HistoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        HistoryModel model = list.get(position);
        holder.location.setText(model.getLocation());
        holder.heartRateHighest.setText(model.getHeartRateHighest());
        holder.heartRateLowest.setText(model.getHeartRateLowest());
        holder.heartRateAve.setText(model.getHeartRateAverage());
        holder.date.setText(model.getDate());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView location, heartRateAve, heartRateHighest, heartRateLowest, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.location_Textview);
            heartRateAve = itemView.findViewById(R.id.averageHeartRate_Textview);
            heartRateHighest = itemView.findViewById(R.id.highestHeartRate_Textview);
            heartRateLowest = itemView.findViewById(R.id.lowestHeartRate_Textview);
            date = itemView.findViewById(R.id.date_Textview);
        }
    }
}
