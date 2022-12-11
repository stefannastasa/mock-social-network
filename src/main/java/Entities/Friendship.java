package Entities;

import Utils.FriendshipStatus;

import java.time.LocalDateTime;

public class Friendship {

    private LocalDateTime dateTime;
    private FriendshipStatus status;
    private final EID user;

    public Friendship(LocalDateTime dateTime, FriendshipStatus status, EID user) {
        this.dateTime = dateTime;
        this.status = status;
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public EID getUser() {
        return user;
    }
}
