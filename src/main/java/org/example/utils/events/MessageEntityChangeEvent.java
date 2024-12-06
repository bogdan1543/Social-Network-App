package org.example.utils.events;

import org.example.domain.Message;
import org.example.domain.User;

public class MessageEntityChangeEvent implements Event {
    private ChangeEventType type;
    private Message data, oldData;

    public MessageEntityChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }
    public MessageEntityChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
