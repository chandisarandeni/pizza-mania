package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private Marker currentMarker;

    // Default location for delivery
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.7749, -122.4194); // San Francisco
    private static final float DEFAULT_ZOOM = 15f;

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
        setDefaultLocation();
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
            // openFullScreenMap();
        });
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