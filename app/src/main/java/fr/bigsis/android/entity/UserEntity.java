package fr.bigsis.android.entity;

import java.util.HashMap;
import java.util.Map;

public class UserEntity {

    private String username;
    private String description;
    private String imageProfileUrl;
    private String firstname;
    private String lastname;

    public UserEntity() {
    }

    public UserEntity(String username, String description, String imageProfileUrl, String firstname, String lastname) {
        this.username = username;
        this.description = description;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("username", username);
        tripMap.put("description", description);
        tripMap.put("imageProfileUrl", imageProfileUrl);
        tripMap.put("pseudonyme", firstname);
        tripMap.put("lastname", lastname);
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
