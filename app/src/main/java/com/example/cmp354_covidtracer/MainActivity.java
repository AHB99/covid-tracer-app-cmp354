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
        if (!sharedPreferences.getString("userEmailId", "").equals("")){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

    }

    public void onSubmitClicked(View view){

        //TODO: Check for uniqueness
        //TODO: Check if empty

        String userName = etName.getText().toString();
        String userEmailId = etEmailId.getText().toString();

        Users newUser = new Users(userName,userEmailId, false);
        DatabaseReference myRef = database.getReference("Users");
        DatabaseReference newUserReference =myRef.push();
        String userDbKey = newUserReference.getKey();
        newUserReference.setValue(newUser);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("userEmailId", userEmailId);
        editor.putString("userDbKey", userDbKey);
        editor.commit();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}