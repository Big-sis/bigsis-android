package fr.bigsis.android.entity;

public class ChatEntity {

    private String sender;
    private String recipient;
    private String message;
    private String type;
    private String message_status;
    private long time;
    private Boolean isTagged;

    public ChatEntity() {
    }

    public ChatEntity(String sender, String recipient, String message, String type, String message_status, long time, Boolean isTagged) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.type = type;
        this.message_status = message_status;
        this.time = time;
        this.isTagged = isTagged;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage_status() {
        return message_status;
    }

    public void setMessage_status(String message_status) {
        this.message_status = message_status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getTagged() {
        return isTagged;
    }

    public void setTagged(Boolean tagged) {
        isTagged = tagged;
    }
}
