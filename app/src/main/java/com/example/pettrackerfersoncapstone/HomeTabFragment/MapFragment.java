package com.example.pettrackerfersoncapstone.HomeTabFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pettrackerfersoncapstone.R;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MapFragment extends Fragment implements OnMapReadyCallback {


    GoogleMap gMap;
    private RelativeLayout locatingLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Marker marker;

    private ImageButton focusPetLocation;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initWidgets(view);

        locatingLayout.setVisibility(View.GONE);
        focusPetLocation.setVisibility(View.GONE);
        SupportMapFragment supportMapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this);


        mapInitialize();

        return view;
    }

    private void initWidgets(View view) {
        locatingLayout = view.findViewById(R.id.noLocation_RelativeLayout);
        focusPetLocation =view.findViewById(R.id.focusUser_Button);

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
                                Log.d("TAG", "Location provider success");


                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("location");
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
                                                if(getContext()!= null){
                                                    locatingLayout.setVisibility(View.GONE);
                                                    focusPetLocation.setVisibility(View.VISIBLE);
                                                    double latitude = Double.parseDouble(latitudeString);
                                                    double longitude = Double.parseDouble(longitudeString);

                                                    if (marker != null){
                                                        marker.remove();
                                                    }

                                                    LatLng latLng1 = new LatLng(latitude, longitude);
                                                    MarkerOptions markerOptions = new MarkerOptions();
                                                    markerOptions.title("Pet Tracker");
                                                    markerOptions.position(latLng1).icon(setIcon((Activity) getContext(), R.drawable.pet_location_icon));

                                                    marker = gMap.addMarker(markerOptions);
                                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 17);



                                                    //Focus user/blind location
                                                    focusPetLocation.setOnClickListener(v->{
                                                        gMap.animateCamera(cameraUpdate);
                                                    });

                                                    if (!gMap.getUiSettings().isZoomControlsEnabled()){
                                                        gMap.animateCamera(cameraUpdate);
                                                    }

                                                }

                                            }


                                        }
                                        else{
                                            Log.d("TAG", "Snapshot does not exist");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("TAG", error.getMessage());

                                    }
                                });

                                Log.d("TAG", "AFTER DATABASE");

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

    @Override
    public void onResume() {
        super.onResume();
    }
}