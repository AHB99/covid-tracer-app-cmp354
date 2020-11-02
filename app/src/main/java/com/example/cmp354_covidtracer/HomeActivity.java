package com.example.cmp354_covidtracer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        tvWelcome.setText("Welcome " + sharedPreferences.getString("userName", ""));

    }
}