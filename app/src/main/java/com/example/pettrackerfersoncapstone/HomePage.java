package com.example.pettrackerfersoncapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pettrackerfersoncapstone.DrawerFragment.HistoryFragment;
import com.example.pettrackerfersoncapstone.DrawerFragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private final  String value = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initWidgets();
        setUpDrawer();
        setUpNameFromHeader();



        Fragment selectedFragment = null;
        if (value.equals("Home")){
            selectedFragment = new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
    }

    private void setUpNameFromHeader() {
        View headerView = navigationView.getHeaderView(0); // Assuming the header is the first view in the navigation view

        TextView nameTextView = headerView.findViewById(R.id.name_Textview);
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    String name = documentSnapshot.getString("name");
                                    nameTextView.setText(name);
                                }
                            }
                        }
                    });
        }
    }

    private void setUpDrawer() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initWidgets() {
        drawerLayout = findViewById(R.id.HomePage_Drawer);
        navigationView = findViewById(R.id.HomePage_Nav_View);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false);
        }
        item.setChecked(true);

        if (itemId == R.id.Home){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new HomeFragment();
        }
        else if (itemId == R.id.History){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);
            selectedFragment = new HistoryFragment();
        }
        else if (itemId == R.id.Logout){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Successfully log out", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

        }

        if (selectedFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();



    }
}