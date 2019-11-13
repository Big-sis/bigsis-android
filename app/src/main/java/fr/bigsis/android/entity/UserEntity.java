package fr.bigsis.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class UserEntity implements Parcelable {

    private String username;
    private String description;
    private String imageProfileUrl;
    private String firstname;
    private String lastname;
    private String userId;
    private boolean creator;

    public UserEntity(String username, String description, String imageProfileUrl, String firstname, String lastname) {
        this.username = username;
        this.description = description;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    protected UserEntity(Parcel in) {
        this.username = in.readString();
        this.description = in.readString();
        this.imageProfileUrl = in.readString();
        this.firstname = in.readString();
        this.lastname = in.readString();
    }

    public UserEntity(String username, String imageProfileUrl, String firstname, String lastname) {
        this.username = username;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public UserEntity(String username, String description, String imageProfileUrl, String firstname, String lastname, boolean creator) {
        this.username = username;
        this.imageProfileUrl = imageProfileUrl;
        this.firstname = firstname;
        this.lastname = lastname;
        this.creator = creator;
        this.description = description;
    }

    public UserEntity() {
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.description);
        dest.writeString(this.imageProfileUrl);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
    }
}
