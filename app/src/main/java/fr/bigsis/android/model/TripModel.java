package fr.bigsis.android.model;

public class TripModel {

    private String fromLocation;
    private String toLocation;
    // TODO dateTime or timestamp, author and chat
    private String thumbnail;
    private int maxParticipants;

    public TripModel(String fromLocation, String toLocation, String thumbnail, int maxParticipants) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.thumbnail = thumbnail;
        this.maxParticipants = maxParticipants;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
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
