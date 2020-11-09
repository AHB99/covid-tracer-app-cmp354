package com.example.cmp354_covidtracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText etName, etEmailId;
    SharedPreferences sharedPreferences;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etEmailId = (EditText) findViewById(R.id.etEmailId);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

//        //If account already set
//        if (!sharedPreferences.getString("userEmailId", "").equals("")){
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
//        }

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

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}