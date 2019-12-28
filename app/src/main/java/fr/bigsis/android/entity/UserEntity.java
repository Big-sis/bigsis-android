package fr.bigsis.android.entity;

import java.util.HashMap;
import java.util.Map;

public class UserEntity {

    private String username;
    private String description;
    private String imageProfileUrl;
    private String firstname;
    private String lastname;
    private String userId;
    private boolean creator;
    private boolean isAdmin;
    private boolean isOnline;
    private String token;
    private String groupCampus;
    private String organism;
    private String lastnameAndFirstname;

    public UserEntity(String username, String description, String imageProfileUrl, String firstname,
                      String lastname, boolean isAdmin, String groupCampus, String organism,
    String lastnameAndFirstname) {
        this.username = username;
        this.description = description;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
        this.isAdmin = isAdmin;
        this.groupCampus = groupCampus;
        this.organism = organism;
        this.lastnameAndFirstname = lastnameAndFirstname;
    }

    public UserEntity(String username, String description, String imageProfileUrl, String firstname,
                      String lastname, boolean creator,
                      boolean isAdmin, boolean isOnline, String userId) {
        this.username = username;
        this.description = description;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
        this.creator = creator;
        this.isAdmin = isAdmin;
        this.isOnline = isOnline;
        this.userId = userId;
    }

    public UserEntity(String username, String description, String imageProfileUrl, String firstname,
                      String lastname,  String groupCampus, String organism, String userId, boolean isAdmin) {
        this.username = username;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
        this.description = description;
        this.isAdmin = isAdmin;
        this.groupCampus = groupCampus;
        this.description = description;
        this.organism = organism;
        this.userId = userId;
    }

    public UserEntity(String username, String description, String imageProfileUrl, String firstname,
                      String lastname, boolean creator, boolean isAdmin, String groupCampus, String organism) {
        this.username = username;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
        this.creator = creator;
        this.description = description;
        this.isAdmin = isAdmin;
        this.groupCampus = groupCampus;
        this.organism = organism;
    }

    public UserEntity() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCreator() {
        return creator;
    }

    public void setCreator(boolean creator) {
        this.creator = creator;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        isOnline = isOnline;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroupCampus() {
        return groupCampus;
    }

    public void setGroupCampus(String groupCampus) {
        this.groupCampus = groupCampus;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getLastnameAndFirstname() {
        return lastnameAndFirstname;
    }

    public void setLastnameAndFirstname(String lastnameAndFirstname) {
        this.lastnameAndFirstname = lastnameAndFirstname;
    }
}
