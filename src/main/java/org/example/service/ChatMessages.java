package org.example.service;

import org.example.domain.Message;
import org.example.domain.User;
import org.example.repository.FriendshipDBRepository;
import org.example.repository.MessageDBRepository;
import org.example.repository.UserDBRepository;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.MessageEntityChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatMessages implements Observable<MessageEntityChangeEvent> {
    private SocialNetwork mainService;
    private MessageDBRepository messageRepository;
    private List<Observer<MessageEntityChangeEvent>> messageObservers;

    public ChatMessages(SocialNetwork mainService, MessageDBRepository messageRepository) {
        this.mainService = mainService;
        this.messageRepository = messageRepository;
        this.messageObservers = new ArrayList<>();
    }

    public Iterable<Message> getUserMessages(User from, User to){
        List<Message> userMessages = new ArrayList<>();
        System.out.println(messageRepository.findAll());
        for (Message message : messageRepository.findAll()){
            message.setFrom(mainService.findUser(message.getIdFrom()));
            message.setTo(mainService.findUser(message.getIdTo()));
            System.out.println(message.getFrom() + "-" + message.getTo());
            if (Objects.equals(message.getIdFrom(), from.getId()) && Objects.equals(message.getIdTo(), to.getId()) || Objects.equals(message.getIdFrom(), to.getId()) && Objects.equals(message.getIdTo(), from.getId())){
                userMessages.add(message);
            }
        }
        return userMessages;
    }

    public void addMessage(Message message){
        message.setId(mainService.getNewUserId());
        messageRepository.save(message);
        notifyObservers(new MessageEntityChangeEvent(ChangeEventType.ADD, message));
    }

    @Override
    public void addObserver(Observer<MessageEntityChangeEvent> e) {
        messageObservers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageEntityChangeEvent> e) {

    }

    @Override
    public void notifyObservers(MessageEntityChangeEvent t) {
        messageObservers.forEach(x->x.update(t));
    }
}
