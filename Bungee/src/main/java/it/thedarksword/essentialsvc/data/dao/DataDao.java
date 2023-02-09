package it.thedarksword.essentialsvc.data.dao;

import java.util.HashMap;

public interface DataDao<R, T> {

    HashMap<R, T> load();
    HashMap<R, T> getData();

    void create(T data);
    void delete(T data);

    void save(T data);
    void saveAll();

    String getSQLQuery(T data);
    String getSQLInsert(T data);
    String getSQLUpdate(T data);
    String getSQLDelete(T data);

}
