package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientBackMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import org.bukkit.entity.Player;

public class BackCommand extends AbstractCommand {

    public BackCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "back");
    }

    @Override
    public void execute(Player player, String[] args) {
        essentialsVC.getMessenger().sendMessage(player, new ClientBackMessage(player.getUniqueId(),
                new LocationObject(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                        player.getLocation().getYaw(), player.getLocation().getPitch())));
        /*EssPlayer essPlayer = essentialsVC.getEssPlayer().get(player.getName());
        if(essPlayer.getLastLocation() == null) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.CANT_BACK));
            return;
        }
        //player.teleport(essPlayer.getLastLocation());
        essentialsVC.getTeleportTask().addTeleport(essPlayer, essPlayer.getLastLocation(),
                new ClientSendConfigMessage(player.getName(), ConfigMessage.BACK),
                ess -> ess.getPlayer().teleport(ess.getLastLocation()));*/
    }
}
