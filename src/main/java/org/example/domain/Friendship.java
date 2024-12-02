package org.example.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Integer>{
    private LocalDateTime dateTime = null;
    private Integer idUser1;
    private Integer idUser2;

    private FriendRequest friendRequestStatus;
    public Friendship(Integer idUser1, Integer idUser2) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.friendRequestStatus = FriendRequest.PENDING;
    }

    public Friendship(Integer idUser1, Integer idUser2, FriendRequest friendRequest) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.friendRequestStatus = friendRequest;
    }
    public Friendship(Integer idUser1, Integer idUser2, LocalDateTime dateTime) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.dateTime = dateTime;
    }

    public FriendRequest getFriendRequestStatus() {
        return friendRequestStatus;
    }

    public void setFriendRequestStatus(FriendRequest friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getIdUser1() {
        return idUser1;
    }

    public Integer getIdUser2() {
        return idUser2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(dateTime, that.dateTime) && Objects.equals(idUser1, that.idUser1) && Objects.equals(idUser2, that.idUser2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, idUser1, idUser2);
    }
}
