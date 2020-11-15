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

    //######################### BELOW CODE WILL BE MOVED TO SERVICE AFTER DEBUGGING ##################################

    //Will be moved to SERVICE later, periodically checking every x mins, saving exposures local DB
    //TODO: Set limit to 2 weeks/Delete older than 2 weeks
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
                            new CheckExposureExistency(ex).execute();
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

    private class CheckExposureExistency extends AsyncTask<Object, Object, Cursor>
    {
        private Exposure exp;
        CheckExposureExistency(Exposure exp){
            this.exp = exp;
        }
        DatabaseConnector databaseConnector =
                new DatabaseConnector(HomeActivity.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            String userIdToSelect = exp.getUserId();
            String positiveIdToSelect  = exp.getPositiveId();
            int timestampToSelect = exp.getLocation().getTs();

            // get a cursor containing all data on given entry
            return databaseConnector.getExposureByUsersAndTimestamp(userIdToSelect,positiveIdToSelect,timestampToSelect);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            if (result.getCount() <= 0){
                new InsertExposure().execute(exp);
            }

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    }

    private class InsertExposure extends AsyncTask<Exposure, Object, Object>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(HomeActivity.this);

        // perform the database access
        @Override
        protected Object doInBackground(Exposure... params)
        {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            databaseConnector.insertExposure(params[0]);
            return null;
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Object result)
        {
            Log.v("abc", "inserted!");
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    }


    public void onViewExposureButtonClicked(View view){
        Intent intent = new Intent(this,ViewExposuresActivity.class);
        startActivity(intent);
    }



}