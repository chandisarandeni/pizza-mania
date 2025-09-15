package com.pizzamania;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pizzamania.context.common.network.NetworkClient;
import com.pizzamania.context.customer.repository.CustomerRepository;
import com.pizzamania.context.order.repository.OrderRepository;
import com.pizzamania.session.SessionManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private Marker currentMarker;


    private final OrderRepository orderRepository = new OrderRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();

    // Default location for delivery
    private static final LatLng DEFAULT_LOCATION = new LatLng(6.8869, 79.8653); // NIBM Colombo

    // Two pizza shops
    private static final LatLng VITO_PIZZA_ARCADE = new LatLng(6.902586321495314, 79.86945111349169);
    private static final LatLng PIZZA_HUT_BORELLA = new LatLng(6.907709110357464, 79.90031025341534);
    private static final float DEFAULT_ZOOM = 15f;

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String VALID_CARD_NUMBER = "4242424242424242";

    private static final String VALID_CARD_HOLDER = "shiran";
    private static final String VALID_CARD_EXPIRY = "1230";
    private static final String VALID_CARD_CVC = "123";

    private final List<Map<String, Object>> cartItems = new ArrayList<>();

    private LatLng chosenShop;


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

        parseCartFromIntent();

        String email = SessionManager.getInstance(this).getEmail();

        if(email != null && !email.isEmpty()) {
            fetchCustomerAndPopulate(email);
        }

        setUpContinueButton();


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

        this.chosenShop = nearestShop;
        // add markers for both shops (optional)
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(VITO_PIZZA_ARCADE).title("Vito Pizza Arcade"));
            mMap.addMarker(new MarkerOptions().position(PIZZA_HUT_BORELLA).title("PizzaHut Borella"));
        }

        // draw route from nearest shop to user
        drawRouteFromShopToUser(nearestShop, user);
    }

    private boolean isSameLocation(LatLng a, LatLng b) {
        if (a == null || b == null) return false;
        final double EPS = 0.0001;
        return Math.abs(a.latitude - b.latitude) < EPS && Math.abs(a.longitude - b.longitude) < EPS;
    }

    private String getShopAddress(LatLng shopLatLng) {
        if (shopLatLng == null) return "Unknown shop address";

        if (isSameLocation(shopLatLng, VITO_PIZZA_ARCADE)) {
            return "Vito Pizza Arcade, Arcade Rd, Colombo"; // replace with actual address if you have it
        } else if (isSameLocation(shopLatLng, PIZZA_HUT_BORELLA)) {
            return "PizzaHut Borella, Borella Rd, Colombo"; // replace with actual address if you have it
        } else {
            return "Unknown shop address";
        }
    }

    private String getShopBranchId(LatLng shopLatLng) {
        if (shopLatLng == null) return "BRANCH_UNKNOWN";

        if (isSameLocation(shopLatLng, VITO_PIZZA_ARCADE)) {
            return "BRANCH_001"; // Vito Pizza Arcade branch ID
        } else if (isSameLocation(shopLatLng, PIZZA_HUT_BORELLA)) {
            return "BRANCH_002"; // Pizza Hut Borella branch ID
        } else {
            return "BRANCH_UNKNOWN";
        }
    }

    private void drawRouteFromShopToUser(LatLng shopLatLng, LatLng userLatLng) {
        // Get shop address for display purposes
        String shopAddress = getShopAddress(shopLatLng);

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
                    runOnUiThread(() -> Toast.makeText(this, "No route found from " + shopAddress, Toast.LENGTH_SHORT).show());
                    return;
                }
                String polyline = routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                final List<LatLng> path = decodePoly(polyline);

                runOnUiThread(() -> {
                    if (mMap == null) return;

                    // Add markers for shop and user
                    mMap.addMarker(new MarkerOptions()
                            .position(shopLatLng)
                            .title("Shop Location")
                            .snippet(shopAddress)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    mMap.addMarker(new MarkerOptions()
                            .position(userLatLng)
                            .title("Delivery Location")
                            .snippet("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

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

                    // Show route confirmation
                    Toast.makeText(this, "Route from " + shopAddress + " to your location", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Route fetch failed from " + shopAddress, Toast.LENGTH_SHORT).show());
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

    private void fetchCustomerAndPopulate(String email) {
        if (email == null || email.isEmpty()) return;

        customerRepository.getCustomerByEmail(email, new NetworkClient.NetworkCallback() {
            @Override
            public void onSuccess(String responseBody) {
                try {
                    // Log the raw response for debugging
                    android.util.Log.d("CheckoutActivity", "Raw API Response: " + responseBody);

                    // Check if response is an array or object
                    JSONObject customerData;
                    if (responseBody.trim().startsWith("[")) {
                        // Response is an array, get the first customer object
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            customerData = jsonArray.getJSONObject(0);
                        } else {
                            runOnUiThread(() -> Toast.makeText(CheckoutActivity.this, "No customer data found", Toast.LENGTH_SHORT).show());
                            return;
                        }
                    } else {
                        // Response is a direct object
                        customerData = new JSONObject(responseBody);
                    }

                    // Extract customer data
                    String name = customerData.optString("name", "");
                    String phone = customerData.optString("phone", "");
                    String address = customerData.optString("address", "");
                    String customerId = customerData.optString("customerId", "");
                    String fetchedEmail = customerData.optString("email", email);

                    // Log extracted values for debugging
                    android.util.Log.d("CheckoutActivity", "Extracted - Name: " + name + ", Phone: " + phone + ", Email: " + fetchedEmail);

                    // Save to session
                    SessionManager.getInstance(CheckoutActivity.this)
                            .saveUser(fetchedEmail, name, phone, address, customerId);

                    // Update UI
                    runOnUiThread(() -> {
                        EditText fullNameText = findViewById(R.id.et_full_name);
                        EditText phoneText = findViewById(R.id.et_mobile_no);
                        EditText emailText = findViewById(R.id.et_email);

                        // Log UI elements found
                        android.util.Log.d("CheckoutActivity", "UI Elements - Name field: " + (fullNameText != null) +
                                          ", Phone field: " + (phoneText != null) +
                                          ", Email field: " + (emailText != null));

                        if (fullNameText != null) {
                            fullNameText.setText(name);
                            android.util.Log.d("CheckoutActivity", "Set name field to: " + name);
                        }
                        if (phoneText != null) {
                            phoneText.setText(phone);
                            android.util.Log.d("CheckoutActivity", "Set phone field to: " + phone);
                        }
                        if (emailText != null) {
                            emailText.setText(fetchedEmail);
                            android.util.Log.d("CheckoutActivity", "Set email field to: " + fetchedEmail);
                        }

                        Toast.makeText(CheckoutActivity.this, "Data loaded: " + name + " | " + phone, Toast.LENGTH_LONG).show();
                    });

                } catch (Exception e) {
                    android.util.Log.e("CheckoutActivity", "Parse error: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(CheckoutActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Exception error) {
                runOnUiThread(() -> Toast.makeText(CheckoutActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

    }

    public void setUpContinueButton () {
        Button continueButton = findViewById(R.id.btn_continue);

        if(continueButton == null) return;


        continueButton.setOnClickListener(v -> {
            // Read UI fields (these are prefilled from session when available)
            EditText fullNameText = findViewById(R.id.et_full_name);
            EditText phoneText = findViewById(R.id.et_mobile_no);
            EditText emailText = findViewById(R.id.et_email);

            String name = fullNameText != null ? fullNameText.getText().toString().trim() : "";
            String phone = phoneText != null ? phoneText.getText().toString().trim() : "";
            String email = emailText != null ? emailText.getText().toString().trim() : "";

            // Read payment fields
            EditText cardNumberEt = findViewById(R.id.et_card_number);
            EditText cardHolderEt = findViewById(R.id.et_cardholder_name);
            EditText cardExpiryEt = findViewById(R.id.et_exp_date);
            EditText cardCvcEt = findViewById(R.id.et_cvc);

            String cardNumber = cardNumberEt != null ? cardNumberEt.getText().toString().trim() : "";
            String cardHolder = cardHolderEt != null ? cardHolderEt.getText().toString().trim() : "";
            String cardExpiry = cardExpiryEt != null ? cardExpiryEt.getText().toString().trim() : "";
            String cardCvc = cardCvcEt != null ? cardCvcEt.getText().toString().trim() : "";

            // Simple hardcoded validation: must match constants
            boolean paymentAuthorized = cardNumber.equals(VALID_CARD_NUMBER)
                    && cardHolder.equalsIgnoreCase(VALID_CARD_HOLDER)
                    && cardExpiry.equals(VALID_CARD_EXPIRY)
                    && cardCvc.equals(VALID_CARD_CVC);

            if (!paymentAuthorized) {
                Toast.makeText(CheckoutActivity.this, "Payment failed: invalid card details", Toast.LENGTH_LONG).show();
                return;
            }

            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            //collect customer info
            String customerId = SessionManager.getInstance(this).getCustomerId();
            String customerName = SessionManager.getInstance(this).getName();
            String customerPhone = SessionManager.getInstance(this).getPhone();
            String customerEmail = SessionManager.getInstance(this).getEmail();

            if (customerId == null || customerId.isEmpty()) {
                Toast.makeText(this, "Customer ID missing. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(customerName == null || customerName.isEmpty()) {
                Toast.makeText(this, "Customer name missing. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(customerPhone == null || customerPhone.isEmpty()) {
                Toast.makeText(this, "Customer phone missing. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(customerEmail == null || customerEmail.isEmpty()) {
                Toast.makeText(this, "Customer email missing. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            //branch shop info
            String branchId = getShopBranchId(this.chosenShop);

            //total amount
            double totalAmount = getIntent().getDoubleExtra("total_price", 0.0);

            //build order
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("customerId", customerId != null ? customerId : "");
            orderMap.put("email", email != null ? email : "");
            orderMap.put("customerName", name);
            orderMap.put("customerPhone", phone);
            orderMap.put("branchId", branchId);
            orderMap.put("orderAmount", totalAmount);
            orderMap.put("orderStatus", "CREATED");

// Format orderDate like: 2025-09-14T16:00:00Z
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            orderMap.put("orderDate", sdf.format(new Date()));

// Build items array
            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (Map<String, Object> item : cartItems) {
                Map<String, Object> map = new HashMap<>();
                map.put("productId", item.get("id"));          // must be "PIZZA001", not just 1
                map.put("productName", item.get("name"));      // ensure cartItems has "name"
                map.put("productSize", item.get("size"));      // ensure cartItems has "size"
                map.put("quantity", item.get("quantity"));
                map.put("price", item.get("price"));
                itemsList.add(map);
            }
            orderMap.put("items", itemsList);

// Add payment info
            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("method", "card");
            paymentMap.put("card_last4", cardNumber.length() >= 4
                    ? cardNumber.substring(cardNumber.length() - 4)
                    : cardNumber);
            paymentMap.put("authorized", true);
            orderMap.put("payment", paymentMap);

            //send to the repository
            orderRepository.createOrderFromMap(orderMap, new OrderRepository.RepoCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                        finish(); // close checkout activity
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(CheckoutActivity.this, "Order failed: " + error, Toast.LENGTH_LONG).show());
                    android.util.Log.e("CheckoutActivity", "Order failed: " + error);
                    Log.d("CheckoutActivity", "data: " +orderMap);
                }
            });

        });

    }

    private void parseCartFromIntent() {
        String cartJson = getIntent().getStringExtra("cart_items_json");
        if (cartJson == null || cartJson.isEmpty()) return;

        try {
            org.json.JSONArray arr = new org.json.JSONArray(cartJson);
            cartItems.clear(); // just in case

            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject obj = arr.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();

                map.put("id", obj.getString("id"));
                map.put("price", obj.getDouble("price"));
                map.put("quantity", obj.getInt("quantity"));
                map.put("name", obj.getString("name"));       // add this
                map.put("size", obj.optString("size", "Medium")); // optional default

                cartItems.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            cartItems.clear(); // fallback to empty list
        }
    }


}
