package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSpawnMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import org.bukkit.entity.Player;

public class SpawnCommand extends AbstractCommand {

    public SpawnCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "spawn");
    }

    @Override
    public void execute(Player player, String[] args) {
        essentialsVC.getMessenger().sendMessage(player, new ClientSpawnMessage(player.getName(),
                new LocationObject(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                        player.getLocation().getYaw(), player.getLocation().getPitch()),
                ClientSpawnMessage.SpawnType.LOAD));
    }
}
