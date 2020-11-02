package com.example.cmp354_covidtracer;

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

//
//        DatabaseReference myRef = database.getReference("Users");
//
//        myRef.child("User1").setValue(new Users("Johnathan"));
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Users value = dataSnapshot.child("User1").getValue(Users.class);
//                Toast.makeText(MainActivity.this, value.getName(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//            }
//        });

    }

    public void onSubmitClicked(View view){

        //TODO: Check for uniqueness
        //TODO: Check if empty

        String userName = etName.getText().toString();
        String userEmailId = etEmailId.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("userEmailId", userEmailId);
        editor.commit();

        Users newUser = new Users(userName,userEmailId, false);
        DatabaseReference myRef = database.getReference("Users");
        DatabaseReference newUserReference =myRef.push();
        newUserReference.setValue(newUser);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}