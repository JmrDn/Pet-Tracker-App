package com.example.pettrackerfersoncapstone.HomeTabFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pettrackerfersoncapstone.R;
import com.example.pettrackerfersoncapstone.Utils.DateAndTimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Date;


public class HeartRateFragment extends Fragment {
    private static final String KEY_VISITED_BEFORE = "visitedBefore";
    private boolean visitedBefore = false;
    private TextView heartRateTV, timeTV;
    private String oldHeartRate = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);
        initWidgets(view);
        setUpHeartRate();


        return view;
    }

    private void setUpHeartRate() {
        DatabaseReference heartRateDR = FirebaseDatabase.getInstance().getReference("heart_rate");

        heartRateDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Date date = new Date();
                    String currentHeartRate = snapshot.child("bpm").getValue().toString();
                    String time = DateAndTimeUtils.timeFormat(date);

                    heartRateTV.setText(currentHeartRate);
                    timeTV.setText(time);

                    if (currentHeartRate != null && currentHeartRate.equals(oldHeartRate)){
                        heartRateTV.setText("0");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage());

            }
        });


    }

    private void refresh(int i, String currentHeartRate) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentHeartRate != null && currentHeartRate.equals(oldHeartRate)){
                    heartRateTV.setText("0");
                }
                else{
                    heartRateTV.setText(currentHeartRate);

                }
                setUpHeartRate();
            }
        }, i);
    }

    private void initWidgets(View view) {
        heartRateTV = view.findViewById(R.id.heartRate_Textview);
        timeTV = view.findViewById(R.id.time_Textview);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the activity
        outState.putBoolean(KEY_VISITED_BEFORE, visitedBefore);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}