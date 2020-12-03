package com.example.cmp354_covidtracer;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ViewExposuresActivity  extends ListActivity
{
    public static final String ROW_ID = "row_id"; // Intent extra key
    private ListView exposureListView; // the ListActivity's ListView
    private CursorAdapter exposureAdapter; // adapter for ListView
    SharedPreferences sharedPreferences;

    //TS: Adapter that exposes data from a Cursor to a ListView widget.
    //The Cursor must include a column named "_id" or this class will not work

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // call super's onCreate

        //TS: 1. create list view and set it event handler
        exposureListView = getListView(); // Get the activity's list view widget
        exposureListView.setOnItemClickListener(viewContactListener);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        //TS: 2. create a cursor for the list view
        // map each contact's name to a TextView in the ListView layout
        //String[] from = new String[] { "name"};
        //int[] to = new int[] { R.id.contactTextView, R.id.emailTextView };//from contact_list_item.xml

        String[] from = new String[] { "timestamp" };
        int[] to = new int[] { R.id.tvTimestamp};//from contact_list_item.xml

        exposureAdapter = new SimpleCursorAdapter(
                ViewExposuresActivity.this, R.layout.contact_list_item, null, from, to, 0); //ts: code update to include flag

        //TS: 3. link the cursor to the list view
        setListAdapter(exposureAdapter); // set contactView's adapter
    } // end method onCreate

    @Override
    protected void onResume()
    {
        super.onResume(); // call super's onResume method

        // create new GetContactsTask and execute it
        String userIdToSearch = sharedPreferences.getString("userDbKey","");

        new GetExposuresTask().execute(userIdToSearch); //TS: this does not work in OnCreate
    } // end method onResume

    @Override
    protected void onStop()
    {
        Cursor cursor = exposureAdapter.getCursor(); // get current Cursor
        exposureAdapter.changeCursor(null); // adapted now has no Cursor

        if (cursor != null) //ts: code update from cursor.deactivate()
            cursor.close(); // release the Cursor's resources (like file handlers and avoid memory leak)

        super.onStop();
    } // end method onStop

    // performs database query outside GUI thread
    private class GetExposuresTask extends AsyncTask<String, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(ViewExposuresActivity.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(String... params)
        {
            databaseConnector.open();
            Log.v("abc", "searching for: "+params[0]);

            // get a cursor containing call contacts
            return databaseConnector.getAllExposuresOfUser(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            exposureAdapter.changeCursor(result); // set the adapter's Cursor
            Log.v("abc", "retrieved: "+result.getCount());

            databaseConnector.close();
        } // end method onPostExecute
    } // end class GetContactsTask

    // event listener that responds to the user touching a contact's name
    // in the ListView
    AdapterView.OnItemClickListener viewContactListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3)
        {
            // create an Intent to launch the ViewContact Activity
            Intent viewContact = new Intent(ViewExposuresActivity.this, ViewSingleExposureActivity.class);

            // pass the selected contact's row ID as an extra with the Intent
            viewContact.putExtra(ROW_ID, arg3);
            startActivity(viewContact); // start the ViewContact Activity
        } // end method onItemClick
    }; // end viewContactListener
}