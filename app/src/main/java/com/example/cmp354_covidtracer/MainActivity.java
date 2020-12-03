package com.example.cmp354_covidtracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etName, etEmailId;
    Button submitBtn;
    SharedPreferences sharedPreferences;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etEmailId = (EditText) findViewById(R.id.etEmailId);
        submitBtn = (Button) findViewById(R.id.btnSubmit);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

//        //If account already set
//        if (!sharedPreferences.getString("userEmailId", "").equals("")){
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
//        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123)
            if(grantResults.length == 1&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(this, HomeActivity.class));
        else
            Toast.makeText(this, "Please enable GPS to use this app", Toast.LENGTH_LONG).show();
    }

    //TODO: Add loading spinner while DB is checked
    public void onSubmitClicked(View view){

        final String userName = etName.getText().toString().toLowerCase();
        final String userEmailId = etEmailId.getText().toString().toLowerCase();

        if (userName.isEmpty() || userEmailId.isEmpty()){
            Toast.makeText(this, "Please fill fields", Toast.LENGTH_SHORT).show();
            return;
        }

        final DatabaseReference myRef = database.getReference("Users");

        myRef.orderByChild("emailId").equalTo(userEmailId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userKey = "";
                Boolean userPositive = false;
                if (dataSnapshot.hasChildren()) {
                    //This will loop ONCE over the SINGLE child with the email id
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        userKey = ds.getKey();
                        userPositive = ds.child("covidPositive").getValue(Boolean.class);
                    }
                }
                else{
                    Users newUser = new Users(userName,userEmailId, false);
                    DatabaseReference newUserReference = myRef.push();
                    userKey = newUserReference.getKey();
                    newUserReference.setValue(newUser);
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", userName);
                editor.putString("userEmailId", userEmailId);
                editor.putBoolean("userPositive", userPositive);
                editor.putString("userDbKey", userKey);
                editor.commit();

                userPermissions();
//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void userPermissions() {
        // if GPS is not enabled, start GPS settings activity
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please activate GPS settings", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        //CMP354 2019 update: get user permission to use GPS
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },123);
        else
            startActivity(new Intent(this, HomeActivity.class));
    }
}