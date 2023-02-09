package it.thedarksword.essentialsvc.data.domain;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.objets.Tuple;
import it.thedarksword.essentialsvc.utils.ServerLocationObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@Data
public class UserAccount implements GenericData<UUID, UserAccount> {

    private DataDao<UUID, UserAccount> dataDao;

    private UUID uuid;
    private String name;

    private @Nullable ProxiedPlayer player;
    private ServerLocationObject lastLocation;
    private Tuple<UUID, Long> tpaRequest;
    private Teleporting teleporting;
    private UUID lastMessaged;

    private ServerLocationObject attempt;

    public UserAccount(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public ServerLocationObject getLastLocation() {
        return lastLocation;
    }

    public Tuple<UUID, Long> getTpaRequest() {
        return tpaRequest;
    }

    public Teleporting getTeleporting() {
        return teleporting;
    }

    public UUID getLastMessaged() {
        return lastMessaged;
    }

    public ServerLocationObject getAttempt() {
        return attempt;
    }

    @Override
    public DataDao<UUID, UserAccount> getDao() {
        return dataDao;
    }

    @Override
    public UserAccount getData() {
        return this;
    }

    public void setDataDao(DataDao<UUID, UserAccount> dataDao) {
        this.dataDao = dataDao;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayer(ProxiedPlayer player) {
        this.player = player;
    }

    public void setLastLocation(ServerLocationObject lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void setTpaRequest(Tuple<UUID, Long> tpaRequest) {
        this.tpaRequest = tpaRequest;
    }

    public void setTeleporting(Teleporting teleporting) {
        this.teleporting = teleporting;
    }

    public void setLastMessaged(UUID lastMessaged) {
        this.lastMessaged = lastMessaged;
    }

    public void setAttempt(ServerLocationObject attempt) {
        this.attempt = attempt;
    }

    @AllArgsConstructor
    @Data
    public static final class Teleporting {
        private final LocationObject location;
        private final UserAccount target;
        private int seconds;
        private final Consumer<UserAccount> execute;

        public void decrementSeconds() {
            seconds--;
        }
    }
}
