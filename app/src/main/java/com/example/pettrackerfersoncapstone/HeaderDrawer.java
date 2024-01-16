package com.example.pettrackerfersoncapstone;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HeaderDrawer extends AppCompatActivity {
    private TextView nameTV;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_header);

        nameTV = findViewById(R.id.name_Textview);

        setUpName();


    }

    private void setUpName() {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId!= null){
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    String name = documentSnapshot.getString("name");
                                    nameTV.setText(name);
                                }
                            }
                        }
                    });
        }
    }
}
