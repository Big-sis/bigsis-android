package fr.bigsis.android.entity;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class EventEntity {

    private Date dateEvent;
    private String eventId;
    private String titleEvent;
    private String descriptionEvent;
    private String imageEvent;
    private String routeEventImage;
    private String adressEvent;

    public EventEntity() {
    }

    public EventEntity(Date date, String titleEvent, String descriptionEvent, String imageEvent, String routeEventImage, String adressEvent) {
        this.dateEvent = date;
        this.titleEvent = titleEvent;
        this.descriptionEvent = descriptionEvent;
        this.imageEvent = imageEvent;
        this.routeEventImage = routeEventImage;
        this.adressEvent = adressEvent;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("titleEvent", titleEvent);
        tripMap.put("descriptionEvent", descriptionEvent);
        tripMap.put("imageEvent", imageEvent);
        tripMap.put("routeEventImage", routeEventImage);
        tripMap.put("adressEvent", adressEvent);
        return tripMap;
    }

    public Date getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
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

    public String getDescriptionEvent() {
        return descriptionEvent;
    }

    public void setDescriptionEvent(String descriptionEvent) {
        this.descriptionEvent = descriptionEvent;
    }

    public String getImageEvent() {
        return imageEvent;
    }

    public void setImageEvent(String imageEvent) {
        this.imageEvent = imageEvent;
    }

    public String getRouteEventImage() {
        return routeEventImage;
    }

    public void setRouteEventImage(String routeEventImage) {
        this.routeEventImage = routeEventImage;
    }

    public String getAdressEvent() {
        return adressEvent;
    }

    public void setAdressEvent(String adressEvent) {
        this.adressEvent = adressEvent;
    }
}
