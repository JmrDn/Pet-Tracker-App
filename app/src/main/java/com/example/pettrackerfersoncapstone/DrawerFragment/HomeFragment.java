package com.example.pettrackerfersoncapstone.DrawerFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pettrackerfersoncapstone.Adapter.MyViewPagerAdapter;
import com.example.pettrackerfersoncapstone.HomeTabFragment.HeartRateFragment;
import com.example.pettrackerfersoncapstone.HomeTabFragment.MapFragment;
import com.example.pettrackerfersoncapstone.R;
import com.example.pettrackerfersoncapstone.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;


public class HomeFragment extends Fragment {

    private RelativeLayout locationLayout, heartRateLayout;
    private TextView locationTV, heartRateTV;
    private double latitude = 0;
    private double longitude = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initWidgets(view);
        setupDefaultTabLayout();
        setupTableLayout();
        setTodayData();
        getDataAverage();
        return view;
    }

    private void getDataAverage() {

        Date currentDateAndTime = new Date();
        String dateForDocumentName = DateAndTimeUtils.dateForDocumentName(currentDateAndTime);
        CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getUid()).collection("daily_history")
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


                                    long heartRate = 0;
                                    int totalHeartRate = 0;
                                    int heartRateAve = 0;
                                    int heartRateDataLength = 0;
                                    int highestHeartRate = 0;
                                    int lowestHeartRate = 400;

                                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                                        heartRate = (long) documentSnapshot.get("heart_rate");


                                        if(heartRate != 0){
                                            if (heartRate > highestHeartRate)
                                                highestHeartRate = (int)heartRate;
                                            if (heartRate < lowestHeartRate)
                                                lowestHeartRate = (int) heartRate;
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

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getUid()).collection("daily_history")
                .document(DateAndTimeUtils.dateForDocumentName(date));

        HashMap<String, Object> averageDocument = new HashMap<>();
        averageDocument.put("bpmAverage", heartRateAve);
        averageDocument.put("date", DateAndTimeUtils.dateFormat(date));
        averageDocument.put("latitude", latitude);
        averageDocument.put("longitude", longitude);
        averageDocument.put("dateId", DateAndTimeUtils.dateForDocumentName(date));
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

    private void setTodayData() {
        DatabaseReference locationDR = FirebaseDatabase.getInstance().getReference("location");

        locationDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String latitudeString = snapshot.child("latitude").getValue().toString();
                    String longitudeString = snapshot.child("longitude").getValue().toString();

                    latitude = Double.parseDouble(latitudeString);
                    longitude = Double.parseDouble(longitudeString);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage());

            }
        });

        DatabaseReference heartRateDR = FirebaseDatabase.getInstance().getReference("heart_rate");

        heartRateDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String heartRateString = snapshot.child("bpm").getValue().toString();
                    int heartRate = Integer.parseInt(heartRateString);

                    Date currentDateAndTime = new Date();
                    String timeStamp = DateAndTimeUtils.dateAndTime(currentDateAndTime);
                    String dateForDocumentName = DateAndTimeUtils.dateForDocumentName(currentDateAndTime);

                   CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().getUid()).collection("daily_history")
                            .document(dateForDocumentName).collection(dateForDocumentName);

                    HashMap<String, Object> todayHeartRateAndLocation = new HashMap<>();

                    if (heartRate != 0 && heartRateString != null){
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

    @SuppressLint("ClickableViewAccessibility")
    private void setupTableLayout() {

        if (getContext()!= null){
            locationLayout.setOnTouchListener(new View.OnTouchListener() {

              
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    locationLayout.setBackgroundResource(R.drawable.selected_tab);
                    locationTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                    heartRateLayout.setBackgroundResource(R.drawable.unselected_tab);
                    heartRateTV.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

                    Fragment fragment = new MapFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, fragment).commit();

                    return false;
                }
            });

            heartRateLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    heartRateLayout.setBackgroundResource(R.drawable.selected_tab);
                    heartRateTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                    locationLayout.setBackgroundResource(R.drawable.unselected_tab);
                    locationTV.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

                    Fragment fragment = new HeartRateFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, fragment).commit();

                    return false;
                }
            });
        }
    }

    private void setupDefaultTabLayout() {
        heartRateLayout.setBackgroundResource(R.drawable.selected_tab);
        heartRateTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        locationLayout.setBackgroundResource(R.drawable.unselected_tab);
        locationTV.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

        Fragment fragment = new HeartRateFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }


    private void initWidgets(View view) {
        locationLayout = view.findViewById(R.id.locationLayout);
        heartRateLayout = view.findViewById(R.id.heartRateLayout);

        locationTV = view.findViewById(R.id.locationTextview);
        heartRateTV = view.findViewById(R.id.heartRate_Textview);
    }
}