package fr.bigsis.android.entity;

import java.util.HashMap;
import java.util.Map;

public class UserEntity {

    private String username;
    private String description;
    private String imageProfileUrl;
    private String pseudonyme;

    public UserEntity() {
    }

    public UserEntity(String username, String description, String imageProfileUrl, String pseudonyme) {
        this.username = username;
        this.description = description;
        this.imageProfileUrl = imageProfileUrl;
        this.pseudonyme = pseudonyme;
    }


    public Map<String, Object> toHashMap() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("username", username);
        tripMap.put("description", description);
        tripMap.put("imageProfileUrl", imageProfileUrl);
        tripMap.put("pseudonyme", pseudonyme);
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

    public String getPseudonyme() {
        return pseudonyme;
    }

    public void setPseudonyme(String pseudonyme) {
        this.pseudonyme = pseudonyme;
    }
}
