package ru.itis.studyhelper.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T>{
    Long save(T entity);
    void delete(Long id);
    void update(T entity);
    Optional<T> findByID(Long id);
    List<T> findAll();
}
