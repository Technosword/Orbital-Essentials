package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSpawnMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "setspawn");
    }

    @Override
    public void execute(Player player, String[] args) {
        Location location = player.getLocation();
        essentialsVC.getMessenger().sendMessage(player, new ClientSpawnMessage(player.getName(),
                new LocationObject(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()),
                ClientSpawnMessage.SpawnType.SAVE));
    }
}
