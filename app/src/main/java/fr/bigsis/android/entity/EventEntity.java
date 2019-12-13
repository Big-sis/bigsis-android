package fr.bigsis.android.entity;

import java.util.Date;

public class EventEntity {

    private Date dateStart;
    private Date dateEnd;
    private String eventId;
    private String titleEvent;
    private String addressEvent;
    private String image;
    private String createdBy;

    public EventEntity() {
    }

    public EventEntity(Date dateStart, Date dateEnd, String eventId, String titleEvent, String addressEvent, String image, String createdBy) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.eventId = eventId;
        this.titleEvent = titleEvent;
        this.image = image;
        this.createdBy = createdBy;
        this.addressEvent = addressEvent;
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
}
