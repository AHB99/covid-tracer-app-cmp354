package com.example.cmp354_covidtracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    SharedPreferences sharedPreferences;
    ToggleButton tglBtnPcr;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tglBtnPcr = (ToggleButton) findViewById(R.id.tglBtnPcr);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        tvWelcome.setText("Welcome " + sharedPreferences.getString("userName", ""));




    }

    public void onPcrToggled(View view){
        final DatabaseReference dRef = database.getReference("Users");
        //Need to define field to search for by "orderBychild"
        //Then "equalTo()" to search by value
        //Then add a listener for when it finishes retrieving it on another thread
        dRef.orderByChild("emailId").equalTo(sharedPreferences.getString("userEmailId", "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //This will return all children with the email id (only 1 hopefully) so this for-loop iterates ONCE
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //We now have the unique key of the user
                    String userKey = ds.getKey();
                    //We set the new PCR value based on toggle button
                    dRef.child(userKey).child("covidPositive").setValue(tglBtnPcr.isChecked());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}