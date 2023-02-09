package it.thedarksword.essentialsvc.data.repository.impl;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import it.thedarksword.essentialsvc.data.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImpl implements DataRepository<UUID, UserAccount> {

    private final DataDao<UUID, UserAccount> dataDao;

    public UserRepositoryImpl(DataDao<UUID, UserAccount> dataDao) {
        this.dataDao = dataDao;
    }

    @Override
    public List<UserAccount> getData() {
        return new ArrayList<>(dataDao.getData().values());
    }

    @Override
    public UserAccount get(UUID query) {
        return dataDao.getData().get(query);
    }

    @Override
    public void create(UserAccount data) {
        dataDao.create(data);
    }

    @Override
    public void edit(UserAccount data) {
        dataDao.save(data);
    }

    @Override
    public void delete(UserAccount data) {
        dataDao.delete(data);
    }

}
