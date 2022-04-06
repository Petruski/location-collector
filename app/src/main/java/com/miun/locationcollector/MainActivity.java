package com.miun.locationcollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION = 100;
    private final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get views
        gpsDataView = findViewById(R.id.textViewGPSData);

        // init data members
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {


            }
        };

        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_ACCESS_FINE_AND_COARSE_LOCATION);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationListener = new LocationListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double latitude;
                    double longitude;

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    gpsDataView.append(latitude + " " + longitude + "\n");
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // permission denied
            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
    }
}