package it.thedarksword.essentialsvc.data.repository;

import java.util.List;

public interface DataRepository<R, T> {

    List<T> getData();
    T get(R query);

    void create(T data);
    void edit(T data);
    void delete(T data);

}
