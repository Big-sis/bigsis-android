package fr.bigsis.android.entity;

import java.util.Date;

public class EventEntity {

    private Date date;
    private String eventId;
    private String titleEvent;
    private String image;
    private String createdBy;

    public EventEntity() {
    }

    public EventEntity(Date date, String eventId, String titleEvent, String image, String createdBy) {
        this.date = date;
        this.eventId = eventId;
        this.titleEvent = titleEvent;
        this.image = image;
        this.createdBy = createdBy;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
