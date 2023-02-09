package it.thedarksword.essentialsvc.data.repository.impl;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.dao.impl.HomeDaoImpl;
import it.thedarksword.essentialsvc.data.domain.HomeData;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import it.thedarksword.essentialsvc.data.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomeRepositoryImpl implements DataRepository<Integer, HomeData> {

    private final DataDao<Integer, HomeData> dataDao;

    public HomeRepositoryImpl(DataDao<Integer, HomeData> dataDao) {
        this.dataDao = dataDao;
    }

    @Override
    public List<HomeData> getData() {
        return new ArrayList<>(dataDao.getData().values());
    }

    @Override
    public HomeData get(Integer query) {
        return dataDao.getData().get(query);
    }

    public Optional<HomeData> get(String query, UUID player) {
        return ((HomeDaoImpl) dataDao).get(query, player);
    }

    @Override
    public void create(HomeData data) {
        dataDao.create(data);
    }

    @Override
    public void edit(HomeData data) {
        dataDao.save(data);
    }

    @Override
    public void delete(HomeData data) {
        dataDao.delete(data);
    }

}
