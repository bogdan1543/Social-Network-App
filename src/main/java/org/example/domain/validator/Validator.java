package org.example.domain.validator;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
