package org.example.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Integer>{
    private Integer idFrom;
    private Integer idTo;
    private User from;
    private User to;
    private String message;
    private LocalDateTime date;

    public Message(String message, User from, User to, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }
    public Message(String message, Integer from, Integer to, LocalDateTime date) {
        this.idFrom = from;
        this.idTo = to;
        this.message = message;
        this.date = date;
    }

    public Integer getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(Integer idFrom) {
        this.idFrom = idFrom;
    }

    public Integer getIdTo() {
        return idTo;
    }

    public void setIdTo(Integer idTo) {
        this.idTo = idTo;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
        this.idFrom = from.getId();
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
        this.idTo = to.getId();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return message;
    }
}
