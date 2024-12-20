package org.example.domain.validator;

import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.repository.InMemoryRepository;
import org.example.repository.UserDBRepository;

import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {

    private final UserDBRepository repo;

    public FriendshipValidator(UserDBRepository repo) {
        this.repo = repo;
    }

    @Override
    public void validate(Friendship entity) throws ValidationException {

        Optional<User> u1 = repo.findOne(entity.getIdUser1());
        Optional<User> u2 = repo.findOne(entity.getIdUser2());

        if (entity.getIdUser1() == null || entity.getIdUser2() == null)
            throw new ValidationException("The id can't be null! ");
        if (u1.isEmpty() || u2.isEmpty())
            throw new ValidationException("The id doesn't exist! ");
    }
}