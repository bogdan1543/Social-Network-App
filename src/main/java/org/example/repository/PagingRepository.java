package org.example.repository;

import org.example.domain.Entity;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAllOnPage(Pageable pageable, User user);

}

