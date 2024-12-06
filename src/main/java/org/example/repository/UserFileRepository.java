package org.example.repository;

import org.example.domain.User;
import org.example.domain.validator.Validator;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<Integer, User> {

    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        //TODO: implement method
        User user = new User(null, null, attributes.get(1), attributes.get(2));
        user.setId(Integer.parseInt(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
