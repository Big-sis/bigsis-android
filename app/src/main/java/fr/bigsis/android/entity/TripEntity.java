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
    private String sharedIn;
    private String organism;

    private String createdBy;

    public TripEntity() {
    }

    public TripEntity(String from, String to, Date date, String createdBy, String sharedIn, String organism) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.createdBy = createdBy;
        this.sharedIn = sharedIn;
        this.organism = organism;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("from", from);
        tripMap.put("to", to);
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getSharedIn() {
        return sharedIn;
    }

    public void setSharedIn(String sharedIn) {
        this.sharedIn = sharedIn;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }
}
