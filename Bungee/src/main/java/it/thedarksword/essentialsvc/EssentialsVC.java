package it.thedarksword.essentialsvc;

import it.thedarksword.essentialsvc.config.ConfigValue;
import it.thedarksword.essentialsvc.config.DatabaseConfig;
import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.dao.impl.HomeDaoImpl;
import it.thedarksword.essentialsvc.data.dao.impl.SpawnDaoImpl;
import it.thedarksword.essentialsvc.data.dao.impl.UserDaoImpl;
import it.thedarksword.essentialsvc.data.domain.HomeData;
import it.thedarksword.essentialsvc.data.domain.SpawnData;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import it.thedarksword.essentialsvc.data.repository.DataRepository;
import it.thedarksword.essentialsvc.data.repository.impl.HomeRepositoryImpl;
import it.thedarksword.essentialsvc.data.repository.impl.SpawnRepositoryImpl;
import it.thedarksword.essentialsvc.data.repository.impl.UserRepositoryImpl;
import it.thedarksword.essentialsvc.database.DatabaseManager;
import it.thedarksword.essentialsvc.database.process.task.DataProcessTask;
import it.thedarksword.essentialsvc.help.Help;
import it.thedarksword.essentialsvc.listeners.PlayerListener;
import it.thedarksword.essentialsvc.messaging.AbstractMessenger;
import it.thedarksword.essentialsvc.messaging.Messenger;
import it.thedarksword.essentialsvc.tasks.TeleportTask;
import it.thedarksword.essentialsvc.yaml.Configuration;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class EssentialsVC extends Plugin {

    private ConfigValue configValue;

    private DatabaseManager databaseManager;

    private DataDao<UUID, UserAccount> userDao;
    private DataDao<Integer, HomeData> homeDao;
    private DataDao<String, SpawnData> spawnDao;

    private DataRepository<UUID, UserAccount> userRepository;
    private DataRepository<String, SpawnData> spawnRepository;
    private HomeRepositoryImpl homeRepository;

    private Messenger messenger;

    private TeleportTask teleportTask;

    private final ArrayList<String> servers = new ArrayList<>();

    private Help help;

    @Override
    public void onEnable() {
        Configuration configuration = new Configuration(new File(getDataFolder(), "config.yml"), getResourceAsStream("config.yml"));

        try {
            configuration.autoload();
        } catch (IOException e) {
            onDisable();
            return;
        }

        configValue = new ConfigValue(configuration);

        help = new Help(configuration);

        databaseManager = new DatabaseManager(new DatabaseConfig(configuration));
        databaseManager.init();

        userDao = new UserDaoImpl(databaseManager);
        userDao.load();
        userRepository = new UserRepositoryImpl(userDao);

        homeDao = new HomeDaoImpl(databaseManager);
        homeDao.load();
        homeRepository = new HomeRepositoryImpl(homeDao);

        spawnDao = new SpawnDaoImpl(databaseManager);
        spawnDao.load();
        spawnRepository = new SpawnRepositoryImpl(spawnDao);

        messenger = new Messenger(this);

        teleportTask = new TeleportTask(this);
        getProxy().getScheduler().schedule(this, teleportTask, 1, 1, TimeUnit.SECONDS);

        getProxy().registerChannel(AbstractMessenger.CHANNEL);

        registerListeners();
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel(AbstractMessenger.CHANNEL);

        DataProcessTask.stopTask(databaseManager.process.task);
        databaseManager.process.task.queue.forEach(data -> {
            if (data.getValue()) databaseManager.controller.saveData(data.getKey());
            else databaseManager.controller.deleteData(data.getKey());
        });
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, messenger);
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

}
