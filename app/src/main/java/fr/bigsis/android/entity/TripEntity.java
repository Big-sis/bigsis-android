package fr.bigsis.android.entity;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class TripEntity {
    private Date date;
    private String tripId;
    private String from;
    private String to;
    private String image;

    public TripEntity() {
    }

    public TripEntity(String from, String to, Date date, String image) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.image = image;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("from", from);
        tripMap.put("to", to);
        tripMap.put("image", image);
        return tripMap;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
