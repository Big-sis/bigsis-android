package fr.bigsis.android.entity;

import java.util.Date;

public class GroupChatEntity {
    private String title;
    private String imageGroup;
    private Date date;
    private String lastMessage;
    private String idGroup;

    public GroupChatEntity() {
    }

    public GroupChatEntity(String title, String imageGroup, Date date, String lastMessage) {
        this.title = title;
        this.imageGroup = imageGroup;
        this.date = date;
        this.lastMessage = lastMessage;
    }

    public GroupChatEntity(String title, String imageGroup, Date date) {
        this.title = title;
        this.imageGroup = imageGroup;
        this.date = date;
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
}
