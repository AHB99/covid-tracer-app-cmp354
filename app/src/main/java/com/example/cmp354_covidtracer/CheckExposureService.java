package com.example.cmp354_covidtracer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CheckExposureService extends Service {

    private Timer timer = null;
    private SharedPreferences sharedPreferences;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        startTimer();
    }

    @Override
    public void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkAllExposures();
            }
        }, 1000, 1000*5);
    }

    private void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    //######################### BELOW CODE WILL BE MOVED TO SERVICE AFTER DEBUGGING ##################################

    //Will be moved to SERVICE later, periodically checking every x mins, saving exposures local DB
    //TODO: Set limit to 2 weeks/Delete older than 2 weeks
    private void checkAllExposures() {
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
                                        UserLocation posLoc = positiveUsersLocation.getValue(UserLocation.class);
                                        UserLocation currLoc = currentUsersLocation.getValue(UserLocation.class);
                                        if (UserLocation.isExposure(posLoc,currLoc)){
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
                new DatabaseConnector(getApplicationContext());

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            String userIdToSelect = exp.getUserId();
            String positiveIdToSelect  = exp.getPositiveId();
            long timestampToSelect = exp.getLocation().getTs();

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
                new DatabaseConnector(getApplicationContext());

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
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Object result)
        {
            Log.v("abc", "inserted!");
            databaseConnector.close(); // close database connection
            sendNotification("Oh no! You have been in contact with a COVID positive user!");
        } // end method onPostExecute
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String text)
    {
        // create the intent for the notification
        Intent notificationIntent = new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);

        // create the variables for the notification
        CharSequence tickerText = "COVID Exposure Warning!";
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = text;

        NotificationChannel notificationChannel = new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        // create the notification and set its data
        Notification notification = new NotificationCompat
                .Builder(this, "Channel_ID")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("Channel_ID")
                .build();

        final int NOTIFICATION_ID = 1;
        manager.notify(NOTIFICATION_ID, notification);
    }

}
