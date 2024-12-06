package org.example.domain.validator;

import org.example.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String err = "";

        if (entity.getMessage().isEmpty()) {
            err += "The message cannot be void";
        }

        if (!err.isEmpty()) {
            throw new ValidationException(err);
        }
    }
}
