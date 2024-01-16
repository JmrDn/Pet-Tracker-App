package com.example.pettrackerfersoncapstone.DrawerFragment;

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

import com.example.pettrackerfersoncapstone.Adapter.HistoryAdapter;
import com.example.pettrackerfersoncapstone.Model.HistoryModel;
import com.example.pettrackerfersoncapstone.R;
import com.example.pettrackerfersoncapstone.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<HistoryModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);

        setUpRecyclerview();

        return view;
    }

    private void setUpRecyclerview() {
        list = new ArrayList<>();
        adapter = new HistoryAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                .collection("daily_history");

        if (collectionReference!= null){

            Query query = collectionReference.orderBy("date", Query.Direction.ASCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty() & querySnapshot != null){
                                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        if (documentSnapshot.exists()){
                                            long heartRateAveLong = documentSnapshot.getLong("bpmAverage");
                                            long heartRateHighestLong = documentSnapshot.getLong("highest_heart_rate");
                                            long heartRateLowestLong = documentSnapshot.getLong("lowest_heart_rate");
                                            long latitudeLong = documentSnapshot.getLong("latitude");
                                            long longitudeLong = documentSnapshot.getLong("longitude");

                                            String heartRateAveString = String.valueOf(heartRateAveLong);
                                            String heartRateHighestString = String.valueOf(heartRateHighestLong);
                                            String heartRateLowestString = String.valueOf(heartRateLowestLong);
                                            String date = DateAndTimeUtils.wordDateFormat(documentSnapshot.getString("date"));

                                            double latitude = (double) latitudeLong;
                                            double longitude = (double) longitudeLong;
                                            String address = getCompleteAddressString(latitude, longitude);

                                            if(address.isEmpty())
                                                address = "Philippines";

                                            list.add(new HistoryModel(address, heartRateHighestString,
                                                    heartRateAveString, heartRateLowestString,date));

                                            if (adapter != null)
                                                adapter.notifyDataSetChanged();


                                        }
                                    }
                                }
                            }
                            else {
                                Log.d("TAG", task.getException().getMessage());
                            }
                        }
                    });
        }
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