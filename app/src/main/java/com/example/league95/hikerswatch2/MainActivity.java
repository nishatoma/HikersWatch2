package com.example.league95.hikerswatch2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Get location manager
    LocationManager locationManager;
    //location listener
    LocationListener locationListener;
    //Our text views
    TextView lat, lng, accuracy, altitude, addressText;

    //permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }

    }

    public void startListening() {
        // We have perm but still need to check!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //Method to update location
    @SuppressLint("SetTextI18n")
    public void updateLocationInfo(Location location) {

        Log.i("Location", location.toString());
        String formatLat = String.format("%.4f", location.getLatitude());
        String formatng = String.format("%.4f", location.getLongitude());
        lat.setText("Latitue: " + formatLat);
        lng.setText("Longtitude: " + formatng);
        accuracy.setText("Accuracy: " + String.valueOf(location.getAccuracy()));
        altitude.setText("Altitude: " + String.valueOf(location.getAltitude()));
        //Now get details from location using geocoder
        //Reverse geolocation basically
        //Remember getApplicationContext
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        //Address
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses != null && addresses.size() >= 1) {
                Address add = addresses.get(0);
                //Update the address text view
                String result = "\n";
                if (add.getSubThoroughfare() != null) {
                    result += add.getSubThoroughfare() + " ";
                }
                if (add.getThoroughfare() != null) {
                    result += add.getThoroughfare() + "\n";
                }
                if (add.getLocality() != null) {
                    result += add.getLocality() + "\n";
                }
                if (add.getPostalCode() != null) {
                    result += add.getPostalCode() + "\n";
                }
                if (add.getCountryName() != null) {
                    result += add.getCountryName() + "\n";
                }

                addressText.setText("Address: " + result);
            }

        } catch (IOException e) {
            System.out.println("Location does not exist!");
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Location manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Assign text views on start.
        lat = findViewById(R.id.lat);
        lng = findViewById(R.id.lng);
        accuracy = findViewById(R.id.accuracy);
        altitude = findViewById(R.id.altitude);
        addressText = findViewById(R.id.address);

        //Asking permissions
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //then grant permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //We already got permission
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                //do something now that we have permission
                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }


    }
}
