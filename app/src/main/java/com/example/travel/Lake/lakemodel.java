package com.example.travel.Lake;

public class lakemodel {
    private String imageUrl;
    private String name;
    private String description;
    private String distance;
    private String type;

    public lakemodel(String imageUrl, String name, String description, String distance) {
        this.imageUrl = imageUrl; ;
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.type = "lake";
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
