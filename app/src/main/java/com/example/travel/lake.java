package com.example.travel;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel.Lake.LakeAdapter;
import com.example.travel.Lake.lakemodel;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class lake extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake);
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.lakeRecyclerView); // Replace with your RecyclerView ID
        if (intent != null) {
            String nameToSearch = intent.getStringExtra("Nature");

            // Call the method to fetch temple data
            fetchNatureData(nameToSearch);
        }
    }
    // Get the intent that started this activity
    // Check if there is data passed through the intent
    private void fetchNatureData(String nameToSearch) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urls.BASE_URL + urls.GET_LAKE_DATA)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Process JSON response data here

                    runOnUiThread(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseData);
                            ArrayList<lakemodel> lakeDataList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("Name");
                                String imageUrl = jsonObject.optString("image");
                                 // Replace ipAddress with your machine's local IP address
                                String finaleimageUrl = urls.IMAGE_URL + imageUrl;

                                String description = jsonObject.optString("description");

                                lakemodel lakeData = new lakemodel(finaleimageUrl, name, description, "Distance"); // Add appropriate distance here
                                lakeDataList.add(lakeData);
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(lake.this));
                            adapter = new LakeAdapter(lakeDataList, lake.this,"lake");
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}
