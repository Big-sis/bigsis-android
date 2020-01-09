package fr.bigsis.android.entity;

import java.util.Date;

public class EventEntity {

    private Date dateStart;
    private Date dateEnd;
    private String eventId;
    private String titleEvent;
    private String addressEvent;
    private String image;
    private String description;
    private String sharedIn;
    private String organism;
    private double latDestination;
    private double lngDestination;
    private String createdBy;
    private boolean isAlertAvailable;

    public EventEntity() {
    }

    public EventEntity(Date dateStart, Date dateEnd, String titleEvent, String addressEvent,
                       String image, String description, String createdBy,String sharedIn, String organism,
                       double latDestination, double lngDestination, boolean isAlertAvailable) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.titleEvent = titleEvent;
        this.image = image;
        this.description = description;
        this.createdBy = createdBy;
        this.addressEvent = addressEvent;
        this.sharedIn = sharedIn;
        this.organism = organism;
        this.addressEvent = addressEvent;
        this.latDestination = latDestination;
        this.lngDestination = lngDestination;
        this.isAlertAvailable = isAlertAvailable;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitleEvent() {
        return titleEvent;
    }

    public void setTitleEvent(String titleEvent) {
        this.titleEvent = titleEvent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAddressEvent() {
        return addressEvent;
    }

    public void setAddressEvent(String addressEvent) {
        this.addressEvent = addressEvent;
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

    public double getLatDestination() {
        return latDestination;
    }

    public void setLatDestination(double latDestination) {
        this.latDestination = latDestination;
    }

    public double getLngDestination() {
        return lngDestination;
    }

    public void setLngDestination(double lngDestination) {
        this.lngDestination = lngDestination;
    }

    public boolean isAlertAvailable() {
        return isAlertAvailable;
    }

    public void setAlertAvailable(boolean alertAvailable) {
        isAlertAvailable = alertAvailable;
    }
}
