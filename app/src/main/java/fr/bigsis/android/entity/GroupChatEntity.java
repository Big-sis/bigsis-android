package fr.bigsis.android.entity;

import java.util.Date;

public class GroupChatEntity {
    private String title;
    private String imageGroup;
    private Date date;
    private String lastMessage;
    private String idGroup;
    private String organism;
    private String sharedIn;

    public GroupChatEntity() {
    }

    public GroupChatEntity(String title, String imageGroup, Date date, String lastMessage, String organism, String sharedIn) {
        this.title = title;
        this.imageGroup = imageGroup;
        this.date = date;
        this.lastMessage = lastMessage;
        this.organism = organism;
        this.sharedIn = sharedIn;
    }

    public GroupChatEntity(String title, String lastMessage, Date date, String organism) {
        this.title = title;
        this.date = date;
        this.lastMessage = lastMessage;
        this.organism = organism;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageGroup() {
        return imageGroup;
    }

    public void setImageGroup(String imageGroup) {
        this.imageGroup = imageGroup;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getSharedIn() {
        return sharedIn;
    }

    public void setSharedIn(String sharedIn) {
        this.sharedIn = sharedIn;
    }
}
