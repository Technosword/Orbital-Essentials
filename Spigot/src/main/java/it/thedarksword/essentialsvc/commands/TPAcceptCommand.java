package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientTeleportToPlayerMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.objets.Tuple;
import it.thedarksword.essentialsvc.player.EssPlayer;
import org.bukkit.entity.Player;

public class TPAcceptCommand extends AbstractCommand {

    public TPAcceptCommand(EssentialsVC essentialsVC) {
	   super(essentialsVC, "tpaccept");
    }

    @Override
    public void execute(Player player, String[] args) {
	   EssPlayer essPlayer = essentialsVC.getEssPlayer().get(player.getName());
	   Tuple<String, Long> tuple = essPlayer.getTpaRequest();
	   if (tuple == null || System.currentTimeMillis() > tuple.getB()) {
		  essPlayer.setTpaRequest(null);
		  essentialsVC.getMessenger().sendMessage(player, new ClientTeleportToPlayerMessage(player.getName(), "",
				new LocationObject(player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
					   player.getLocation().getYaw(), player.getLocation().getPitch()),
				ClientTeleportToPlayerMessage.Cause.TPACCEPT));
		  //essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.NO_TPA_TO_ACCEPT));
		  return;
	   }

	   Player target = essentialsVC.getServer().getPlayer(tuple.getA());
	   if (target == null) {
		  //essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.PLAYER_NOT_FOUND));
		  essentialsVC.getMessenger().sendMessage(player, new ClientTeleportToPlayerMessage(player.getName(), "",
				new LocationObject(player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
					   player.getLocation().getYaw(), player.getLocation().getPitch()),
				ClientTeleportToPlayerMessage.Cause.TPACCEPT));
		  return;
	   }

	   //target.teleport(player);
	   essentialsVC.getTeleportTask().addTeleport(essentialsVC.getEssPlayer().get(target.getName()), player.getLocation(), null, ess -> ess.getPlayer().teleport(player.getLocation()));

	   essPlayer.setTpaRequest(null);

	   essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.TPA_ACCEPT, new String[][]{{"{player}", target.getName()}}));
	   essentialsVC.getMessenger().sendMessage(target, new ClientSendConfigMessage(target.getName(), ConfigMessage.TPA_ACCEPTED, new String[][]{{"{player}", player.getName()}}));
    }
}
