package org.example.domain;

import java.time.LocalDateTime;

public class ReplyMessage extends Message{
    Message repliedMessage;
    public ReplyMessage(String message, User from, User to, Message repliedMessage) {
        super(message, from, to, LocalDateTime.now());
        this.repliedMessage = repliedMessage;
    }

    public Message getRepliedMessage() {
        return repliedMessage;
    }

    public void setRepliedMessage(Message repliedMessage) {
        this.repliedMessage = repliedMessage;
    }
}
