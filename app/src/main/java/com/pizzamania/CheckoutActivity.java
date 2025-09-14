package com.pizzamania;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pizzamania.session.SessionManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private Marker currentMarker;

    // Default location for delivery
    private static final LatLng DEFAULT_LOCATION = new LatLng(6.8869, 79.8653); // NIBM Colombo
    private static final float DEFAULT_ZOOM = 15f;

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupMap();
        setupBackButton();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        double checkoutTotal = getIntent().getDoubleExtra("total_price", 0.0);
        TextView totalText = findViewById(R.id.tv_total_bill);
        if(totalText != null) {
            totalText.setText(String.format("$%.2f", checkoutTotal));
        }

        EditText fullNameText = findViewById(R.id.et_full_name);
        EditText phoneText = findViewById(R.id.et_mobile_no);
        EditText emailText = findViewById(R.id.et_email);

        String email = SessionManager.getInstance(this).getEmail();

        String name = SessionManager.getInstance(this).getName();

        String phone = SessionManager.getInstance(this).getPhone();

        if(email != null && !email.isEmpty()) {
            Toast.makeText(this, "Logged in as: " + email, Toast.LENGTH_LONG).show();
            emailText.setText(email);
        }

        if(name != null && !name.isEmpty()) {
            fullNameText.setText(name);
        }

        if(phone != null && !phone.isEmpty()) {
            phoneText.setText(phone);
        }
        else {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_LONG).show();
        }
    }

    private void setupMap() {
        // Create MapView programmatically
        mapView = new MapView(this);

        // Add MapView to the container
        FrameLayout mapContainer = findViewById(R.id.map_container);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (int) (180 * getResources().getDisplayMetrics().density) // 180dp height
        );
        mapView.setLayoutParams(layoutParams);
        mapContainer.addView(mapView);

        // Initialize map
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);

        // Add click listener to open full screen map
        mapView.setOnClickListener(v -> openFullScreenMap());
    }

    private void setupBackButton() {
        findViewById(R.id.iv_back_arrow).setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMapSettings();
        // Enable real location tracking instead of just setting default location
        enableMyLocationAndCenter();
    }

    private void setupMapSettings() {
        if (mMap == null) return;

        // Map settings - Enable zoom controls for better user experience
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Set map click listener for selecting delivery location
        mMap.setOnMapClickListener(latLng -> {
            setLocation(latLng);
            // Optional: Open full screen on click
            openFullScreenMap();
        });
    }

    private void enableMyLocationAndCenter() {
        if (mMap == null) return;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                // ignore - we checked permission
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                updateLocationMarker();
                                moveCamera(currentLocation, DEFAULT_ZOOM);
                            } else {
                                // fallback to default
                                setDefaultLocation();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        setDefaultLocation();
                    });

        } else {
            // Permission not granted -> request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            // meanwhile show default
            setDefaultLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted, enable location
                enableMyLocationAndCenter();
            } else {
                Toast.makeText(this, "Location permission denied — using default location.", Toast.LENGTH_SHORT).show();
                setDefaultLocation();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void openFullScreenMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        // Pass current location to full screen map
        if (currentLocation != null) {
            intent.putExtra("latitude", currentLocation.latitude);
            intent.putExtra("longitude", currentLocation.longitude);
        }
        startActivity(intent);
    }

    private void setDefaultLocation() {
        currentLocation = DEFAULT_LOCATION;
        updateLocationMarker();
        moveCamera(currentLocation, DEFAULT_ZOOM);
    }

    public void setLocation(LatLng location) {
        this.currentLocation = location;
        updateLocationMarker();
        moveCamera(location, DEFAULT_ZOOM);
    }

    private void updateLocationMarker() {
        if (mMap == null || currentLocation == null) return;

        // Remove existing marker
        if (currentMarker != null) {
            currentMarker.remove();
        }

        // Add new marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentLocation)
                .title("Delivery Location")
                .snippet("Your pizza will be delivered here");

        currentMarker = mMap.addMarker(markerOptions);
    }

    private void moveCamera(LatLng location, float zoom) {
        if (mMap != null && location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}