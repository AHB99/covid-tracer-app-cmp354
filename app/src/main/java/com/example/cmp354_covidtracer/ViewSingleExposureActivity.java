package com.example.cmp354_covidtracer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewSingleExposureActivity extends AppCompatActivity {

    private long rowID; // selected contact's name
    private TextView dateTextView; // displays contact's name
    private TextView latTextView; // displays contact's phone
    private TextView lngTextView; // displays contact's email
    private Button mapButton; // displays contact's street
    private double lat;
    private double lng;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_exposure);

        // get the EditTexts
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        latTextView = (TextView) findViewById(R.id.latTextView);
        lngTextView = (TextView) findViewById(R.id.lngTextView);
        mapButton = (Button) findViewById(R.id.mapButton);

        // get the selected contact's row ID
        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(ViewExposuresActivity.ROW_ID);
    } // end method onCreate

    // called when the activity is first created
    @Override
    protected void onResume()
    {
        super.onResume();

        // create new LoadContactTask and execute it
        mapButton.setEnabled(false);
        new LoadExposureTask().execute(rowID);

    } // end method onResume

    // performs database query outside GUI thread
    private class LoadExposureTask extends AsyncTask<Long, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(ViewSingleExposureActivity.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOneExposure(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int dateIndex = result.getColumnIndex("timestamp");
            int latIndex = result.getColumnIndex("lat");
            int lngIndex = result.getColumnIndex("lng");

            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM yy HH:mm:ss z yyyy");
            String dateString = formatter.format(new Date(result.getInt(dateIndex)));

            // fill TextViews with the retrieved data
            dateTextView.setText(dateString);
            latTextView.setText(String.valueOf(result.getDouble(latIndex)));
            lngTextView.setText(String.valueOf(result.getDouble(lngIndex)));
            lat = result.getDouble(latIndex);
            lng = result.getDouble(lngIndex);

            mapButton.setEnabled(true);


            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadContactTask

    public void onMapButtonClicked(View view){
        Intent intent = new Intent(ViewSingleExposureActivity.this, ExposureMapActivity.class);

        // pass the selected contact's row ID as an extra with the Intent
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);

        startActivity(intent);
    }

}