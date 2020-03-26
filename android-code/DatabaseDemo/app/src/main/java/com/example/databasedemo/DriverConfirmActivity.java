/*
DriverConfirmActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Shows map to driver and asks to confirm pickup
 * @author Micheal Antifaoff
 */
public class DriverConfirmActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    TextView waiting;
    Button driverConfirmPickupButton, cancelPickupButton;
    boolean driverReady = false;
    ListenerRegistration registration;

    /**
     * Called when activity is created
     * displays map and confirm button
     * {@link RiderNewRequestActivity#addRequest(Request, String, String) addRequest}
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_confirm);
        waiting = findViewById(R.id.waiting_for_rider);
        driverConfirmPickupButton = findViewById(R.id.driver_confirm_pickup_button);
        cancelPickupButton = findViewById(R.id.cancel_pickup_button_confirm_activity);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_pickup_map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        final String riderUsername = i.getStringExtra("riderUsername");
        final String driverUsername = i.getStringExtra("driverUsername");

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);

        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Request request = documentSnapshot.toObject(Request.class);
                if (driverReady) {
                    driverConfirmPickupButton.setVisibility(View.INVISIBLE);
                    waiting.setVisibility(View.VISIBLE);
                    cancelPickupButton.setVisibility(View.VISIBLE);
                }
                if(request.getRiderConfirmation()&&request.getDriverConfirmation()){
                    Intent i = new Intent(DriverConfirmActivity.this, DriverEndAndPay.class);
                    // Activity expects: final String riderUsername = i.getStringExtra("riderUsername");
                    //                   final String driverUsername = i.getStringExtra("driverUsername");
                    i.putExtra("riderUsername", riderUsername);
                    i.putExtra("driverUsername", driverUsername);
                    startActivity(i);
                    finish();
                }
                if (request.getDriverConfirmation()){
                    driverConfirmPickupButton.setVisibility(View.INVISIBLE);
                    waiting.setVisibility(View.VISIBLE);
                    cancelPickupButton.setVisibility(View.VISIBLE);
                }
            }
        });

        driverConfirmPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Request request = task.getResult().toObject(Request.class);
                            request.driverConfirmation();
                            docRef.set(request);
                            driverReady = true;
                            driverConfirmPickupButton.setVisibility(View.INVISIBLE);
                            waiting.setVisibility(View.VISIBLE);
                            cancelPickupButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        cancelPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);

                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent startRiderOrDriverInitial = new Intent(DriverConfirmActivity.this, RiderDriverInitialActivity.class);
                        // Activity expects:    boolean driver = intent.getBooleanExtra("driver", true);
                        //                      final String username = intent.getStringExtra("username");
                        //                      final String email = intent.getStringExtra("email");
                        startRiderOrDriverInitial.putExtra("driver", true);
                        startRiderOrDriverInitial.putExtra("username", driverUsername);
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        startRiderOrDriverInitial.putExtra("email", email);
                        startActivity(startRiderOrDriverInitial);
                        finish();
                    }
                });
            }
        });


    }

    @Override
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
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