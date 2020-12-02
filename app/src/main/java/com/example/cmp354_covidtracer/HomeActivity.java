package com.example.cmp354_covidtracer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

        startService(new Intent(this, CheckExposureService.class));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123)
            if(grantResults.length == 1&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startService(new Intent(this, GPSService.class));
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