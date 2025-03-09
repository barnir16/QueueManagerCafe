package app.server.dao;

public interface IDao<T> {
    T find(String id);
    boolean save(T entity);
}
