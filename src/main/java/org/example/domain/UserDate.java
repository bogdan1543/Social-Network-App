package org.example.domain;

import java.time.LocalDateTime;

public class UserDate {
    private User user;
    private String firstName;

    private String lastName;
    private LocalDateTime friendSince;

    public UserDate(User user, LocalDateTime friendSince) {
        this.user = user;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.friendSince = friendSince;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
    }

    public LocalDateTime getFriendSince() {
        return friendSince;
    }

    public void setFriendSince(LocalDateTime friendSince) {
        this.friendSince = friendSince;
    }

}
