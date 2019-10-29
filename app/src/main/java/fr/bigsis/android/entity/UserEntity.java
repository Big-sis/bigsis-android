package fr.bigsis.android.entity;

import java.util.HashMap;
import java.util.Map;

public class UserEntity {

    private String username;
    private String description;
    private String imageProfileUrl;

    public UserEntity() {
    }

    public UserEntity(String username, String description, String imageProfileUrl) {
        this.username = username;
        this.description = description;
        this.imageProfileUrl = imageProfileUrl;
    }


    public Map<String, Object> toHashMap() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("username", username);
        tripMap.put("description", description);
        tripMap.put("imageProfileUrl", imageProfileUrl);
        return tripMap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageProfileUrl() {
        return imageProfileUrl;
    }

    public void setImageProfileUrl(String imageProfileUrl) {
        this.imageProfileUrl = imageProfileUrl;
    }
}
