package com.example.travel.searchh;

public class searchmodel {
    private String imageUrl;
    private String name;
    private String description;
    private String distance;
    private String type;

    public searchmodel(String imageUrl, String name, String description) {
        this.imageUrl = imageUrl; ;
        this.name = name;
        this.description = description;

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
