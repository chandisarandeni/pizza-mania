package com.pizzamania;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pizzamania.session.SessionManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private Marker currentMarker;

    // Default location for delivery
    private static final LatLng DEFAULT_LOCATION = new LatLng(6.8869, 79.8653); // NIBM Colombo

    // Two pizza shops
    private static final LatLng VITO_PIZZA_ARCADE = new LatLng(6.902586321495314, 79.86945111349169);
    private static final LatLng PIZZA_HUT_BORELLA = new LatLng(6.907709110357464, 79.90031025341534);
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
                                chooseNearestShopAndDrawRoute();
                            } else {
                                // fallback to default
                                setDefaultLocation();
                                chooseNearestShopAndDrawRoute();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        setDefaultLocation();
                        chooseNearestShopAndDrawRoute();
                    });

        } else {
            // Permission not granted -> request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            // meanwhile show default
            setDefaultLocation();
            chooseNearestShopAndDrawRoute();
        }
    }

    private void chooseNearestShopAndDrawRoute() {
        LatLng user = currentLocation != null ? currentLocation : DEFAULT_LOCATION;

        float[] result1 = new float[1];
        Location.distanceBetween(user.latitude, user.longitude, VITO_PIZZA_ARCADE.latitude, VITO_PIZZA_ARCADE.longitude, result1);

        float[] result2 = new float[1];
        Location.distanceBetween(user.latitude, user.longitude, PIZZA_HUT_BORELLA.latitude, PIZZA_HUT_BORELLA.longitude, result2);

        LatLng nearestShop = (result1[0] <= result2[0]) ? VITO_PIZZA_ARCADE : PIZZA_HUT_BORELLA;

        // add markers for both shops (optional)
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(VITO_PIZZA_ARCADE).title("Vito Pizza Arcade"));
            mMap.addMarker(new MarkerOptions().position(PIZZA_HUT_BORELLA).title("PizzaHut Borella"));
        }

        // draw route from nearest shop to user
        drawRouteFromShopToUser(nearestShop, user);
    }

    private void drawRouteFromShopToUser(LatLng shopLatLng, LatLng userLatLng) {
        final String apiKey = getString(R.string.directions_api_key); // add your key in strings.xml
        final String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&mode=driving&key=%s",
                shopLatLng.latitude, shopLatLng.longitude, userLatLng.latitude, userLatLng.longitude, apiKey
        );

        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Directions API error", Toast.LENGTH_SHORT).show());
                    return;
                }
                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                JSONArray routes = json.optJSONArray("routes");
                if (routes == null || routes.length() == 0) {
                    runOnUiThread(() -> Toast.makeText(this, "No route found", Toast.LENGTH_SHORT).show());
                    return;
                }
                String polyline = routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                final List<LatLng> path = decodePoly(polyline);

                runOnUiThread(() -> {
                    if (mMap == null) return;
                    // draw polyline
                    mMap.addPolyline(new PolylineOptions()
                            .addAll(path)
                            .width(10)
                            .color(Color.BLUE)
                            .geodesic(true));

                    // ensure both endpoints visible
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(shopLatLng);
                    builder.include(userLatLng);
                    for (LatLng p : path) builder.include(p);
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Route fetch failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // decode encoded polyline from Directions API
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lng += dlng;

            double latD = lat / 1E5;
            double lngD = lng / 1E5;
            poly.add(new LatLng(latD, lngD));
        }
        return poly;
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