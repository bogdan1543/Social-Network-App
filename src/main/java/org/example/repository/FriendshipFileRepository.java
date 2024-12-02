package org.example.repository;

import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.validator.Validator;

import java.util.List;

public class FriendshipFileRepository extends AbstractFileRepository<Integer, Friendship> {
    //private final InMemoryRepository<Integer, User> repo;
    public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
        //this.repo = repo;
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        //TODO: implement method
        Friendship friendship = new Friendship(Integer.parseInt(attributes.get(1)), Integer.parseInt(attributes.get(2)));
        friendship.setId(Integer.parseInt(attributes.get(0)));
//        User user1 = repo.findOne(friendship.getIdUser1());
//        User user2 = repo.findOne(friendship.getIdUser2());
//
//        user1.addFriend(user2);
//        user2.addFriend(user1);

        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId() + ";" + entity.getIdUser1() + ";" + entity.getIdUser2();
    }
}
