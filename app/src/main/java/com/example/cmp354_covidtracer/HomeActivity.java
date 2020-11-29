package com.example.cmp354_covidtracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button btnViewExposures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tglBtnPcr = (ToggleButton) findViewById(R.id.tglBtnPcr);
        btnViewExposures = (Button) findViewById(R.id.btnViewExposures);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        tvWelcome.setText("Welcome " + sharedPreferences.getString("userName", ""));
        tglBtnPcr.setChecked(sharedPreferences.getBoolean("userPositive",false));
        //DEBUG
//        checkAllExposures();
        startService(new Intent(this, CheckExposureService.class));
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

    public void onViewExposureButtonClicked(View view){
        Intent intent = new Intent(this,ViewExposuresActivity.class);
        startActivity(intent);
    }
}