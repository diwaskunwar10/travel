package com.example.travel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.travel.HomeAdapter.FeaturedAdapter;
import com.example.travel.HomeAdapter.FeaturedHelperClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView featuredRecycler;
    RecyclerView.Adapter adapter;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MyAPIRequest";
    private ImageView suggestButton;
    private RequestQueue requestQueue;
    private int[] images = new int[]{R.drawable.everest, R.drawable.sagarmatha, R.drawable.sunsetphewa, R.drawable.begnas, R.drawable.manang, R.drawable.mustangcaves, R.drawable.morningpanchase}; // Add your image resources here
    private int currentIndex = 0;
    private ImageView imageView;


    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        featuredRecycler=findViewById(R.id.featured_recycler);

        imageView = findViewById(R.id.imageSlider);
        ImageView suggestButton = findViewById(R.id.suggestButton);;
        RequestQueueSingleton requestQueueSingleton = RequestQueueSingleton.getInstance(this);
        requestQueue = requestQueueSingleton.getRequestQueue();
        featuredRecycler();
        suggestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for location permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request location permission if not granted
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    // If permission is already granted, retrieve precise location
                    try {
                        LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                // Handle location updates if needed
                                double userLatitude = location.getLatitude();
                                double userLongitude = location.getLongitude();
                                sendLocationToFastAPI(userLatitude, userLongitude);
                                locationManager.removeUpdates(this); // Remove updates after receiving precise location
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                            }
                        };

                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager != null) {
                            // Remove previous updates if any
                            locationManager.removeUpdates(locationListener);
                            // Request location updates
                            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                        } else {
                            Toast.makeText(MainActivity.this, "Location Manager is null", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Permission denied. Please grant location access.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex == images.length) {
                    currentIndex = 0;
                }
                imageView.setImageResource(images[currentIndex]);
                currentIndex++;
                handler.postDelayed(this, 2000); // Change image every 2 seconds
            }
        };
        handler.post(runnable);
    }

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        ArrayList<FeaturedHelperClass> featuredLocations=new ArrayList<>();

        featuredLocations.add(new FeaturedHelperClass(R.drawable.sagarmatha,"Mt EVEREST", "Highest Peak"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.sunsetphewa,"Phewa Lake", "Sunset View of Phewa Lake"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.mustangcaves,"Mustang", "Caves of Mustang"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.morningpanchase,"Panchase", "View of Panchase"));
        adapter=new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);



    }


    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS is disabled", Toast.LENGTH_SHORT).show();
                return;
            }

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    locationManager.removeUpdates(this);
//                    Log.e('latlon', "onLocationChanged: "+latitude +' '+longitude );
                    Toast.makeText(getApplicationContext(), ""+latitude + ' '+ longitude, Toast.LENGTH_SHORT).show();
                }
            };

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Permission denied. Please grant location access.", Toast.LENGTH_SHORT).show();
        }
    }



    private void sendLocationToFastAPI(double latitude, double longitude) {
        String url = "http://192.168.1.67:8000/get_location";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("latitude", latitude);
            jsonBody.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("top_5_nearest")) {
                                JSONArray placesArray = response.getJSONArray("top_5_nearest");
                                handleTop5Nearest(placesArray);
                            } else if (response.has("error")) {
                                String errorMessage = response.getString("error");
                                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Unknown response format", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing response from FastAPI", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void handlePlace(JSONObject place) throws JSONException {
                        String name = place.getString("name");
                        double distance = place.getDouble("distance");

                        String message = "Place: " + name + ", Distance: " + distance;
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    private void handleTop5Nearest(JSONArray placesArray) throws JSONException {
                        ArrayList<String> placeNames = new ArrayList<>();
                        ArrayList<String> distances = new ArrayList<>();

                        for (int i = 0; i < placesArray.length(); i++) {
                            JSONObject place = placesArray.getJSONObject(i);
                            String name = place.getString("name");
                            double distance = place.getDouble("distance");

                            placeNames.add(name);
                            distances.add(String.valueOf(distance));
                        }

                        Intent intent = new Intent(MainActivity.this, display.class);
                        intent.putStringArrayListExtra("placeNames", placeNames);
                        intent.putStringArrayListExtra("distances", distances);
                        intent.putExtra("latitude", latitude); // Pass latitude to the display activity
                        intent.putExtra("longitude", longitude); // Pass longitude to the display activity

                        // Pass the requestQueue and TAG (if available)

//                        intent.putExtra("requestQueue", requestQueue);
                        intent.putExtra("TAG", TAG);
                        startActivity(intent);
                    }



                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, "Error sending location to FastAPI", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        jsonObjectRequest.setTag("REQUEST_TAG");

// Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Inside your activity

    // Method to clear session data
    public void logout(View view) {
        // Call the method to clear the session and perform logout
        clearSession();
    }

    // Method to clear session data
    private void clearSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear all stored data related to the session
        editor.clear();
        editor.apply();

        // Navigate to the login screen or perform other necessary actions
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
