package fr.bigsis.android.model;

import java.util.Date;

public class TripModel {

    private String from;
    private String to;
    private Date date;


    // TODO dateTime or timestamp, author and chat
    private String thumbnail;
    private int maxParticipants;

    public TripModel(String from, String to, String thumbnail, int maxParticipants) {
        this.from = from;
        this.to = to;
        this.thumbnail = thumbnail;
        this.maxParticipants = maxParticipants;
    }

    public TripModel(String from, String to, Date date) {
        this.from = from;
        this.to = to;
        this.date = date;
    }

    public TripModel() {
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
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}
