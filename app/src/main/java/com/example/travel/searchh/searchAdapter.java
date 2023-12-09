package com.example.travel.searchh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travel.R;

import java.util.ArrayList;
public class searchAdapter extends RecyclerView.Adapter<searchAdapter.ViewHolder> {

    private ArrayList<searchmodel> searchList; // Replace datamodel with your data model class
    private Context context;
    private String type;
    public searchAdapter(ArrayList<searchmodel> searchList, Context context) {
        this.searchList = searchList;
        this.context = context;
        this.type="search";
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_card_design, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        searchmodel currentItem = searchList.get(position);

        // Set data to views
        Glide.with(context)
                .load(currentItem.getImageUrl())
                .into(holder.imagedisplay);
        holder.namedisplay.setText(currentItem.getName());
        holder.descdisplay.setText(currentItem.getDescription());

        holder.mapButton.setOnClickListener(view -> {
            String placeName = currentItem.getName();
            openGoogleMaps(placeName, view.getContext());
            Toast.makeText(view.getContext(), "Opening Maps for " + placeName, Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public int getItemCount() {
        return searchList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagedisplay;
        Button mapButton;
        TextView namedisplay, descdisplay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagedisplay = itemView.findViewById(R.id.suggest_image);
            namedisplay = itemView.findViewById(R.id.suggest_title);
            descdisplay = itemView.findViewById(R.id.suggest_desc);

            mapButton = itemView.findViewById(R.id.openMapButton);
        }
    }
    private void openGoogleMaps(String placeName, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" + Uri.encode(placeName)));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Google Maps app not found", Toast.LENGTH_SHORT).show();
        }
    }
}