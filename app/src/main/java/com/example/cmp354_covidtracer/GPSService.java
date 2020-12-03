package com.example.cmp354_covidtracer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private SharedPreferences sharedPreferences;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000);
        googleApiClient.connect();

        // Foreground service code
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel_ID")
                .setOngoing(false)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setPriority(Notification.PRIORITY_LOW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("Channel_ID",
                    "Channel_ID", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("Channel_ID");
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
            startForeground(1, builder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null)
//                new InsertLocation().execute(new UserLocation(location.getLatitude(), location.getLongitude(), location.getTime()));
                Log.d("onConnected:", String.valueOf(location.getLatitude() + '|' + location.getLongitude() + '|' + location.getTime()));
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        catch (SecurityException s) {
            Log.d("CMP354","Not able to run location services...");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Suspended", "Connection was suspended");
        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed! Please check your settings and try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            insertLocation(new UserLocation(location.getLatitude(), location.getLongitude(), location.getTime()));
//            new InsertLocation().execute(new UserLocation(location.getLatitude(), location.getLongitude(), location.getTime()));
        Log.d("CMP354",location.toString());
    }

//    private class InsertLocation extends AsyncTask<UserLocation, Void, Void> {
//        @Override
//        protected Void doInBackground(UserLocation... locations) {
//            final DatabaseReference locationsRef = database.getReference("Locations");
//            String userDbKey = sharedPreferences.getString("userDbKey", "");
//            locationsRef.child(userDbKey).push().setValue(locations[0]);
//            return null;
//        }
//    }

    private void insertLocation(UserLocation location) {
        final DatabaseReference locationsRef = database.getReference("Locations");
        String userDbKey = sharedPreferences.getString("userDbKey", "");
        locationsRef.child(userDbKey).push().setValue(location);
    }
}
