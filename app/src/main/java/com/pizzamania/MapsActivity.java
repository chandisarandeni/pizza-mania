package com.pizzamania;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pizzamania.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public interface OnLocationSelectedListener {
        void onLocationSelected(LatLng location, String address);
    }

    private MapView mapView;
    private GoogleMap googleMap;
    private Context context;
    private OnLocationSelectedListener locationListener;
    private LatLng currentLocation;
    private Marker currentMarker;

    // Default location (you can change this to your restaurant location)
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.7749, -122.4194); // San Francisco
    private static final float DEFAULT_ZOOM = 15f;

    public MapsActivity(Context context, ViewGroup parent) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View mapContainer = inflater.inflate(R.layout.activity_maps, parent, false);

        mapView = mapContainer.findViewById(R.id.map_view);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);

        parent.addView(mapContainer);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        setupMap();
        setDefaultLocation();
    }

    private void setupMap() {
        if (googleMap == null) return;

        // Map settings
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        // Set map click listener
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setLocation(latLng);
                // Get address from coordinates
                getAddressFromLocation(latLng);
            }
        });

        // Set marker click listener
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true; // Consume the event
            }
        });
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
        if (googleMap == null || currentLocation == null) return;

        // Remove existing marker
        if (currentMarker != null) {
            currentMarker.remove();
        }

        // Add new marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentLocation)
                .title("Delivery Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentMarker = googleMap.addMarker(markerOptions);
    }

    private void moveCamera(LatLng location, float zoom) {
        if (googleMap == null) return;

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(location, zoom),
                1000,
                null
        );
    }

    private void getAddressFromLocation(LatLng location) {
        if (context == null) return;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);

                if (locationListener != null) {
                    locationListener.onLocationSelected(location, addressText);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to coordinates
            String addressText = String.format("%.6f, %.6f",
                    location.latitude, location.longitude);

            if (locationListener != null) {
                locationListener.onLocationSelected(location, addressText);
            }
        }
    }

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.locationListener = listener;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
    }

    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
    }

    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    public void onLowMemory() {
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    // Method to set location programmatically
    public void updateDeliveryLocation(String address) {
        if (address == null || address.isEmpty()) return;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                setLocation(latLng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}