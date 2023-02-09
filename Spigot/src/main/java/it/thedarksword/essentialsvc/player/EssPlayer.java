package it.thedarksword.essentialsvc.player;

import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Data
public class EssPlayer {

    private Player player;
    private GameMode gameMode;
    private Tuple<String, Long> tpaRequest;
    private Teleport teleport;
    //private String lastMessaged;
    private boolean teleporting;
    private boolean god = false;
    private boolean flying = false;

    public EssPlayer(Player player) {
        this.player = player;
    }

    @AllArgsConstructor
    @Data
    public static final class Teleport {
        private final Location location;
        private int seconds;
        private final Consumer<EssPlayer> execute;
        private final ClientSendConfigMessage message;

        public void decrementSeconds() {
            seconds--;
        }
    }
}
