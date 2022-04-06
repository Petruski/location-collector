package com.miun.locationcollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /* Settings */
    private static final int MIN_TIME = 10;
    private static final int MIN_DISTANCE = 0;
    /* ************************************** */

    private TextView gpsDataView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button startStopButton;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION = 101;
    private final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean isGPSEnabled;
    private boolean permissionsGranted;
    private boolean collectingData = false;

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    return false;
                }
            }
        }
        permissionsGranted = true;
        return true;
    }

    private void enableLocationServices() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get views
        gpsDataView = findViewById(R.id.textViewGPSData);
        startStopButton = findViewById(R.id.startStopButton);

        // init data members
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationListener = new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude;
                double longitude;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                gpsDataView.append(latitude + " " + longitude + "\n");
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        if (!hasPermissions(this, permissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, MY_PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION);
            }
        }

        startStopButton.setOnClickListener(listener -> {
            collectingData = !collectingData;
            if (collectingData && hasPermissions(this, permissions) && isGPSEnabled) {
                startStopButton.setText("Stop");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
            }
            if (!collectingData && hasPermissions(this, permissions) && isGPSEnabled) {
                startStopButton.setText("Start");
                locationManager.removeUpdates(locationListener);
            }
            if (!isGPSEnabled) {
                collectingData = !collectingData;
                enableLocationServices();
            }
            if (!hasPermissions(this, permissions)) {
                collectingData = !collectingData;
                Toast.makeText(MainActivity.this, "Location permissions not enabled, please enable them", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void requestPermissions(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // permission denied
            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            permissionsGranted = false;
        } else {
            Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            permissionsGranted = true;
            if (!isGPSEnabled) {
                enableLocationServices();
            }
        }
    }
}