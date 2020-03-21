/*
RiderEndAndPay
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Activity shown while ride is ongoing, asks rider to click on button once the ride is done
 * @author Michael Antifaoff, Hussein Warsame, Rafaella Graña
 */
public class RiderEndAndPay extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Button riderEndAndPayButton;

    /**
     * Called when activity is created
     * shows rider the ride on the map and a button to indicate if ride has ended. then redirects
     * to {@link GenerateQR GenerateQR}
     * @param {@code Bundle} savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_end_and_pay);
        riderEndAndPayButton = findViewById(R.id.rider_end_and_pay_button);

        Intent i = getIntent();
        final String username = i.getStringExtra("username");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_ride_map);
        mapFragment.getMapAsync(this);

        riderEndAndPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RiderEndAndPay.this, GenerateQR.class);
                i.putExtra("username", username);
                startActivity(i);

            }
        });


    }

    /**
     * when map is loaded, assign it to the map attribute
     * @param {@code GoogleMap}googleMap Map Object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
    }
}
