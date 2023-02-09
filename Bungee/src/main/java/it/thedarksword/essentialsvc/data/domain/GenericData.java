package it.thedarksword.essentialsvc.data.domain;

import it.thedarksword.essentialsvc.data.dao.DataDao;

public interface GenericData<R, T> {

    DataDao<R, T> getDao();
    T getData();

}
