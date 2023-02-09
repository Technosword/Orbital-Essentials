package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHomeMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetHomeCommand extends AbstractCommand {

    public SetHomeCommand(EssentialsVC essentialsVC) {
	   super(essentialsVC, "sethome");
    }

    @Override
    public void execute(Player player, String[] args) {
	   String homeName;
	   if (args.length == 0) {
		  homeName = "home";
	   } else {
		  homeName = args[0];
	   }

	   final Location location = player.getLocation();
	   essentialsVC.getMessenger().sendMessage(player, new ClientHomeMessage(player.getName(), homeName, player.getName(), new LocationObject(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()), ClientHomeMessage.HomeOperation.ADD));
    }
}
