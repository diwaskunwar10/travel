package com.example.travel.TempleAdapters;

public class templemodel {
    private String imageUrl;
    private String name;
    private String description;
    private String distance;
    private String type;

    public templemodel(String imageUrl, String name, String description, String distance) {
        this.imageUrl = imageUrl; ;
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.type = "Temple";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }
}
