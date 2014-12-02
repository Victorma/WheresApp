package com.wheresapp.integration;

import java.util.List;

/**
 * Created by Victorma on 25/11/2014.
 */
public interface DAO<T> {
    public boolean create(T t);
    public T read(T t);
    public boolean update(T t);
    public boolean delete(T t);
    public List<T> discover(T t);
    public List<T> discover(T t, int limit, int page);
}
