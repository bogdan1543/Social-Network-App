package org.example.service;

import org.example.domain.FriendRequest;
import org.example.domain.Friendship;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.domain.validator.ValidationException;
import org.example.repository.FriendshipDBRepository;
import org.example.repository.MessageDBRepository;
import org.example.repository.UserDBRepository;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.MessageEntityChangeEvent;
import org.example.utils.events.UserEntityChangeEvent;
import org.example.utils.observer.Observer;
import org.example.utils.observer.Observable;

import java.time.LocalDateTime;
import java.util.*;


public class SocialNetwork implements Observable<UserEntityChangeEvent>{
    private final UserDBRepository userRepository;
    private FriendshipDBRepository friendshipRepository;

    private List<Observer<UserEntityChangeEvent>> userObservers;


    public SocialNetwork(UserDBRepository userRepository, FriendshipDBRepository friendshipRepository, MessageDBRepository messageRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.userObservers = new ArrayList<>();
    }

    public Integer getNewUserId(){
        Set<Integer> existingIds = new HashSet<>();
        for (User user : userRepository.findAll()) {
            existingIds.add(user.getId());
        }

        Random random = new Random();
        Integer newId;
        do {
            newId = random.nextInt(10000);
        } while (existingIds.contains(newId));

        return newId;
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void addUser(User user){
        user.setId(getNewUserId());
        UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD, user);
        notifyObservers(event);
        userRepository.save(user);
    }

    public User removeUser(Integer id){
        try {
            User u = userRepository.findOne(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
            Vector<Integer> toDelete = new Vector<>();
            getFriendships().forEach(friendship -> {
                if (friendship.getIdUser2().equals(id) || friendship.getIdUser1().equals(id)) {
                    toDelete.add(friendship.getId());
                }
            });
            toDelete.forEach(friendshipRepository::delete);
            User user = userRepository.delete(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
            u.getFriends().forEach(friend -> friend.removeFriend(u));
            notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE, user));
            return user;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user! ");
        } catch (ValidationException v) {
            System.out.println();
        }
        return null;
    }


    public Integer getNewFriendshipId(){
        Set<Integer> existingIds = new HashSet<>();
        friendshipRepository.findAll().forEach(friendship -> {
            existingIds.add(friendship.getId());
        });

        Random random = new Random();
        Integer newId;
        do {
            newId = random.nextInt(10000);
        } while (existingIds.contains(newId));

        return newId;
    }

    public Iterable<Friendship> getFriendships() {
        return friendshipRepository.findAll();
    }
    public void addFriendship(Friendship friendship){
        User user1 = userRepository.findOne(friendship.getIdUser1()).orElseThrow(() -> new ValidationException("User doesn't exist!"));
        User user2 = userRepository.findOne(friendship.getIdUser2()).orElseThrow(() -> new ValidationException("User doesn't exist!"));

        if (getFriendships() != null) {
            getFriendships().forEach(f -> {
                if (f.getIdUser1().equals(friendship.getIdUser1()) && f.getIdUser2().equals(friendship.getIdUser2())) {
                    throw new ValidationException("The friendship already exist! ");
                }
            });
            if (userRepository.findOne(friendship.getIdUser1()).isEmpty() || userRepository.findOne(friendship.getIdUser2()).isEmpty()) {
                throw new ValidationException("User doesn't exist! ");
            }
            if (friendship.getIdUser1().equals(friendship.getIdUser2()))
                throw new ValidationException("IDs can't be the same!!! ");
        }
        friendship.setId(getNewFriendshipId());
        friendshipRepository.save(friendship);

        user1.addFriend(user2);
        user2.addFriend(user1);

    }

    public Friendship getFriendshipById(Integer id1, Integer id2) {
        Iterable<Friendship> lst = friendshipRepository.findAll();
        User u1 = findUser(id1);
        User u2 = findUser(id2);

        if (u1 == null || u2 == null)
            return null;

        for (Friendship fr : lst) {
            if (fr.getIdUser1().equals(u1.getId()) && fr.getIdUser2().equals(u2.getId()) || fr.getIdUser1().equals(u2.getId()) && fr.getIdUser2().equals(u1.getId())) {
                return fr;
            }
        }
        return null;
    }

    public boolean createFriendship(Integer id1, Integer id2) {
        try {
            if (getFriendshipById(id1, id2) != null)
                throw new Exception("The friendship already exist");

            Friendship f = new Friendship(id1, id2, FriendRequest.ACCEPTED);
            friendshipRepository.save(f);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean createFriendRequest(Integer id1, Integer id2) {
        try {
            Friendship friendship = getFriendshipById(id1, id2);
            System.out.println(friendship);
            if (friendship != null && friendship.getFriendRequestStatus() != FriendRequest.REJECTED)
                throw new Exception("The friend request exists and it is not rejected");
            else if (friendship != null && friendship.getFriendRequestStatus() == FriendRequest.REJECTED) {
                friendship.setFriendRequestStatus(FriendRequest.PENDING);
                friendshipRepository.update(friendship);
                notifyObservers(new UserEntityChangeEvent(ChangeEventType.UPDATE, findUser(id1)));
                return true;
            } else {
                Friendship f = new Friendship(id1, id2);
                f.setId(getNewFriendshipId());
                friendshipRepository.save(f);
                notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD, findUser(id1)));
                return true;
            }
        } catch (Exception e) {
            System.out.println("xxxxxxx");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean respondFriendRequest(Friendship friendshipReq, FriendRequest response) {
        try {
            if (friendshipRepository.findOne(friendshipReq.getId()).isEmpty())
                throw new Exception("The friend request does not exist");
            else if (friendshipReq.getFriendRequestStatus() != FriendRequest.PENDING)
                throw new Exception("The friend request is not pending");

            friendshipReq.setFriendRequestStatus(response);
            if (response == FriendRequest.ACCEPTED)
                friendshipReq.setDateTime(LocalDateTime.now());
            else
                friendshipReq.setDateTime(null);

            friendshipRepository.update(friendshipReq);
            notifyObservers(new UserEntityChangeEvent(ChangeEventType.UPDATE, findUser(friendshipReq.getIdUser2())));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public void removeFriendship(Integer id1, Integer id2){
        Integer frId = 0;
        for (Friendship friendship : friendshipRepository.findAll()) {
            if (Objects.equals(friendship.getIdUser1(), id1) && Objects.equals(friendship.getIdUser2(), id2)) {
                frId = friendship.getId();
            } else if (Objects.equals(friendship.getIdUser1(), id2) && Objects.equals(friendship.getIdUser2(), id1)) {
                frId = friendship.getId();
            }
        }
        friendshipRepository.delete(frId).orElseThrow(() -> new ValidationException("Friendship doesn't exist!"));
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE, findUser(id1)));
    }

    public Map<User, LocalDateTime> getUserFriends(User user){
        Map<User, LocalDateTime> userFriends = new HashMap<>();
        for (Friendship f : getFriendships()){
            if(f.getFriendRequestStatus() == FriendRequest.ACCEPTED)
                if (Objects.equals(user.getId(), f.getIdUser1())){
                    userFriends.put(findUser(f.getIdUser2()),f.getDateTime());
                }else if (Objects.equals(user.getId(), f.getIdUser2())) {
                    userFriends.put(findUser(f.getIdUser1()),f.getDateTime());
                }
        }

        return userFriends;
    }

    public Iterable<User> getUserFriendRequests(User user){
        List<User> userFriends = new ArrayList<>();
        for (Friendship f : getFriendships()){
            if(f.getFriendRequestStatus() == FriendRequest.PENDING)
                if (Objects.equals(user.getId(), f.getIdUser2())) {
                    userFriends.add(findUser(f.getIdUser1()));
                }
        }

        return userFriends;
    }

    public User findUser(Integer id) {
        return userRepository.findOne(id).orElseThrow(() -> new ValidationException("No user"));
    }

    public User findUserByUsername(String username){
        for (User user : getUsers()){
            if (Objects.equals(user.getUsername(), username)){
                return user;
            }
        }
        return null;
    }


    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        userObservers.add(e);

    }

//    @Override
//    public void addObserver(Observer<MessageEntityChangeEvent> e) {
//        messageObservers.add(e);
//
//    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent t) {

        userObservers.forEach(x->x.update(t));
    }

}
