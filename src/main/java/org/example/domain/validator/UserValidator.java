package org.example.domain.validator;

import org.example.domain.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        String errorMessage = "";

        if (entity.getFirstName().isEmpty()) {
            errorMessage += "First name can't be null! ";
        }
        if (entity.getLastName().isEmpty()) {
            errorMessage += "Last name can't be null! ";
        }
        System.out.println(errorMessage);
        if (!errorMessage.isEmpty()) {
            throw new ValidationException(errorMessage);
        }
    }
}
