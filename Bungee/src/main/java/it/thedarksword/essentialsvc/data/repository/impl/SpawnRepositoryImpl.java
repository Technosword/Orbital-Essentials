package it.thedarksword.essentialsvc.data.repository.impl;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.domain.SpawnData;
import it.thedarksword.essentialsvc.data.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

public class SpawnRepositoryImpl implements DataRepository<String, SpawnData> {

    private final DataDao<String, SpawnData> dataDao;

    public SpawnRepositoryImpl(DataDao<String, SpawnData> dataDao) {
        this.dataDao = dataDao;
    }

    @Override
    public List<SpawnData> getData() {
        return new ArrayList<>(dataDao.getData().values());
    }

    @Override
    public SpawnData get(String query) {
        return dataDao.getData().get(query);
    }

    @Override
    public void create(SpawnData data) {
        dataDao.create(data);
    }

    @Override
    public void edit(SpawnData data) {
        dataDao.save(data);
    }

    @Override
    public void delete(SpawnData data) {
        dataDao.delete(data);
    }

}