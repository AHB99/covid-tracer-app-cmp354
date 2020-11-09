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

import java.util.ArrayList;

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
        tglBtnPcr.setChecked(sharedPreferences.getBoolean("userPositive",false));
        //DEBUG
        checkAllExposures();
    }

    public void onPcrToggled(View view){
        final DatabaseReference dUserRef = database.getReference("Users");
        String userDbKey = sharedPreferences.getString("userDbKey", "");
        dUserRef.child(userDbKey).child("covidPositive").setValue(tglBtnPcr.isChecked());

        final DatabaseReference dPositivesRef = database.getReference("Positives");

        if (tglBtnPcr.isChecked()){
            dPositivesRef.child(userDbKey).setValue(sharedPreferences.getString("userEmailId", ""));
        }
        else {
            dPositivesRef.child(userDbKey).removeValue();
        }

    }

    //Will be moved to SERVICE later, periodically checking every x mins, saving exposures local DB
    private void checkAllExposures(){
        //Get all positives
        final String currentUserDbKey = sharedPreferences.getString("userDbKey", "");

        final DatabaseReference dPositivesRef = database.getReference("Positives");
        dPositivesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> allPositives = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    allPositives.add(ds.getKey());
                }
                final DatabaseReference dLocationsRef = database.getReference("Locations");

                dLocationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Exposure> allExposures = new ArrayList<>();

                        //Iterate over every POSITIVE user...
                        for (DataSnapshot userHistory : dataSnapshot.getChildren()){
                            //If POSITIVE user AND not current user, iterate over all THEIR locations
                            if (allPositives.contains(userHistory.getKey()) && !userHistory.getKey().equals(currentUserDbKey)){
                                for (DataSnapshot positiveUsersLocation : userHistory.getChildren()){
                                    //Compare this with all CURRENT user's locations
                                    for (DataSnapshot currentUsersLocation : dataSnapshot.child(currentUserDbKey).getChildren()){
                                        Location posLoc = positiveUsersLocation.getValue(Location.class);
                                        Location currLoc = currentUsersLocation.getValue(Location.class);
                                        if (Location.isExposure(posLoc,currLoc)){
                                            allExposures.add(new Exposure(userHistory.getKey(),currentUserDbKey,currLoc));
                                        }

                                    }
                                }
                            }
                        }
                        for (Exposure ex : allExposures){
                            Log.v("abc", ex.toString());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }



}