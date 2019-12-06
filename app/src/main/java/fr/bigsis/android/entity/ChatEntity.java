package fr.bigsis.android.entity;

import java.util.Date;

public class ChatEntity {

    private String id;
    private String chatRoomId;
    private String senderId;
    private String message;
    private String username;
    private Date date;
    private String imageUSer;
    private boolean isTagged;

    public ChatEntity() {
    }

    public ChatEntity(String id, String chatRoomId, String senderId, String username, String message,
                      String imageUSer, Date date, Boolean isTagged) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.username = username;
        this.imageUSer = imageUSer;
        this.date = date;
        this.isTagged = isTagged;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date sent) {
        this.date = sent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUSer() {
        return imageUSer;
    }

    public void setImageUSer(String imageUSer) {
        this.imageUSer = imageUSer;
    }

    public boolean isTagged() {
        return isTagged;
    }

    public void setTagged(boolean tagged) {
        isTagged = tagged;
    }
}
