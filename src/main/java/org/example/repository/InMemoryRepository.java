package org.example.repository;

import org.example.domain.Entity;
import org.example.domain.validator.ValidationException;
import org.example.domain.validator.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Map<ID, E> entities;

    private Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }
    @Override
    public Optional<E> findOne(ID id) {
        if(id == null){
            throw new IllegalArgumentException("Null id");
        }
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity == null){
            throw new IllegalArgumentException("Null entity");
        }
        validator.validate(entity);

        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null){
            throw new IllegalArgumentException("Null id");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null){
            throw new IllegalArgumentException("Null entity");
        }
        validator.validate(entity);
        return Optional.ofNullable(entities.replace(entity.getId(), entity));
    }
}
