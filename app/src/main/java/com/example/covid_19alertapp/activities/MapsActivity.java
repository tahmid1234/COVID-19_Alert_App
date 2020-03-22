package com.example.covid_19alertapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.LogTags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private Button confirmButton;
    private Marker homeMarker = null;

    // home address location
    Location homeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirmButton = findViewById(R.id.confirm_button);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Dhaka and move the camera
        LatLng dhaka = new LatLng(23.7805733, 90.2792376);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dhaka));

        // check if all are needed
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /*
        location selected by long press on map
        ask user to confirm
         */

        Log.d(LogTags.Map_TAG, "onMapLongClick: marker at = "+latLng.toString());

        homeLocation = new Location(getLocalClassName());
        homeLocation.setLatitude(latLng.latitude);
        homeLocation.setLongitude(latLng.longitude);

        if(homeMarker!=null){
            homeMarker.remove();
        }

        homeMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Home"));

        Toast.makeText(
                this,
                "press 'Confirm' to confirm or select another",
                Toast.LENGTH_LONG
        ).show();

        confirmButton.setEnabled(true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        /*
        notify user if location and/or wifi is inactive
         */

        String toastText = "";
        if(!wifiEnabled() && !locationEnabled())
            toastText = "Turn On both WiFi & Location";
        else if(!locationEnabled())
            toastText = "Turn On Location";
        else if(!wifiEnabled())
            toastText = "Turn On WiFi";

        if(!toastText.equals(""))
            Toast.makeText(this
                    , toastText + " to show your location"
                    , Toast.LENGTH_LONG)
                    .show();

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        /*
        press on the blue dot?
         */
        if(location.getAccuracy()>150)
            Toast.makeText(
                    this,
                    "Location Accuracy is LOW. press again please!"+location, Toast.LENGTH_SHORT
            ).show();

    }

    public boolean wifiEnabled(){
        WifiManager wifi = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public boolean locationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void confirmClicked(View view) {
        /*
        take this location and set it as home address
         */

        Log.d(LogTags.Map_TAG, "confirmClicked: location taken = "+homeLocation.toString());

    }
}
