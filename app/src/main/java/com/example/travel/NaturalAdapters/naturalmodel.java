package com.example.travel.NaturalAdapters;

public class naturalmodel {
    private String imageUrl;
    private String name;
    private String description;
    private String distance;
    private String type;

    public naturalmodel(String imageUrl, String name, String description, String distance) {
        this.imageUrl = imageUrl; ;
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.type = "Natural";
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


}
