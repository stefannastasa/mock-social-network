package Entities;

import java.time.LocalDateTime;

public class Message {
    private User sender, receiver;
    private String message;
    private LocalDateTime timeSent;

    public Message(User sender, User receiver, String message, LocalDateTime timeSent) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeSent = timeSent;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }
}
