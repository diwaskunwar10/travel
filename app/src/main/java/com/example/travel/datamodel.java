package com.example.travel;

public class datamodel {
    private String imageUrl;
    private String name;
    private String description;
    private String distance;

    public datamodel(String imageUrl, String name, String description, String distance) {
        this.imageUrl = imageUrl; ;
        this.name = name;
        this.description = description;
        this.distance = distance;
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
