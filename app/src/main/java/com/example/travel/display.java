package com.example.travel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.DisplayAdapter;
import com.example.travel.R;
import com.example.travel.datamodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class display extends AppCompatActivity {
    private ImageView backToHomeImageView;
    private ArrayList<datamodel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        backToHomeImageView = findViewById(R.id.back_to_home); // Initializing inside onCreate

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> placeNames = intent.getStringArrayListExtra("placeNames");
            ArrayList<String> distances = intent.getStringArrayListExtra("distances");

            if (placeNames != null && distances != null && placeNames.size() == distances.size()) {
                for (int i = 0; i < placeNames.size(); i++) {
                    String nameToSearch = placeNames.get(i);
                    String distance = distances.get(i);
                    fetchDataFromDatabase(nameToSearch, distance);
                }
            } else {
                Toast.makeText(this, "Error: Place names and distances mismatch or empty", Toast.LENGTH_SHORT).show();
            }
        }
        backToHomeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate back to the home or previous activity
                Intent intent = new Intent(display.this, MainActivity.class); // Replace HomeActivity with your actual home activity class
                startActivity(intent);
                finish(); // Optional: Finish the current activity to remove it from the stack
            }});
    }


    private void fetchDataFromDatabase(String nameToSearch, String distance) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.67/travel/get_info.php?name=" + nameToSearch);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String jsonResponseStr = response.toString();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonResponseStr);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("Name");
                            String imageUrl = jsonObject.optString("image");
                            String description = jsonObject.optString("description");
                            String baseLocalhostUrl = "http://172.19.96.1/travel/images/"  ; // Replace ipAddress with your machine's local IP address
                            String finaleimageUrl = baseLocalhostUrl + imageUrl;

                            datamodel placeData = new datamodel(finaleimageUrl, name, description, distance);
                            dataList.add(placeData);
                        }

                        runOnUiThread(() -> {
                            RecyclerView recyclerView = findViewById(R.id.myRecyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(display.this));
                            DisplayAdapter adapter = new DisplayAdapter(dataList, display.this);
                            recyclerView.setAdapter(adapter);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        String errorInfo = "Error parsing JSON: " + e.getMessage();
                        Log.e("JSON Parsing Error", errorInfo);
                        runOnUiThread(() -> {
                            Toast.makeText(display.this, errorInfo, Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception error) {
                    error.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(display.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
        thread.start();
    }
}
