package it.thedarksword.essentialsvc.messaging;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.chat.ChatConfig;
import it.thedarksword.essentialsvc.handler.MessageHandler;
import it.thedarksword.essentialsvc.handler.MessageListener;
import it.thedarksword.essentialsvc.messaging.client.ClientHomeSetMessage;
import it.thedarksword.essentialsvc.messaging.common.TeleportStateMessage;
import it.thedarksword.essentialsvc.messaging.common.UpdateBackMessage;
import it.thedarksword.essentialsvc.messaging.server.*;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.player.EssPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
public class SpigotMessageListener implements MessageListener {

    private final EssentialsVC essentialsVC;

    @MessageHandler(ServerTeleportToLocationMessage.class)
    public void handleServerTeleportToLocationMessage(ServerTeleportToLocationMessage message) {
	   Player exactPlayer = essentialsVC.getServer().getPlayer(message.getPlayer());
	   Location location = toBukkitLocation(message.getLocation());
	   if (exactPlayer != null) {
		  exactPlayer.teleport(location, PlayerTeleportEvent.TeleportCause.SPECTATE);
	   } else {
		  essentialsVC.getMessageOnLogin().put(message.getPlayer(), player ->
				essentialsVC.getServer().getScheduler().runTaskLater(essentialsVC, () ->
					   player.teleport(location, PlayerTeleportEvent.TeleportCause.SPECTATE), 1));
	   }
    }

    @MessageHandler(ServerTeleportToPlayerMessage.class)
    public void handleServerTeleportToPlayerMessage(ServerTeleportToPlayerMessage message) {
	   Player player = essentialsVC.getServer().getPlayer(message.getPlayer());
	   Player target = essentialsVC.getServer().getPlayer(message.getTarget());
	   if (target == null) return;
	   if (player != null) {
		  player.teleport(target);
	   } else {
		  essentialsVC.getMessageOnLogin().put(message.getPlayer(), player1 ->
				essentialsVC.getServer().getScheduler().runTaskLater(essentialsVC, () ->
					   player1.teleport(target, PlayerTeleportEvent.TeleportCause.SPECTATE), 1));
	   }
    }

    @MessageHandler(ServerHomeLocationMessage.class)
    public void handleServerHomeLocationMessage(ServerHomeLocationMessage message) {
	   Player player = essentialsVC.getServer().getPlayerExact(message.getPlayer());
	   if (player != null) {
		  Location location = player.getLocation();
		  essentialsVC.getMessenger().sendMessage(player, new ClientHomeSetMessage(player.getName(), message.getHomeName(),
				new LocationObject(player.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
					   location.getYaw(), location.getPitch())));
	   }
    }

    @MessageHandler(ServerUpdatePlayersMessage.class)
    public void handleServerUpdatePlayersMessage(ServerUpdatePlayersMessage message) {
	   essentialsVC.getNetworkPlayers().addAll(message.getPlayers());
    }

    @MessageHandler(ServerUpdatePlayerListMessage.class)
    public void handleServerUpdatePlayerListMessage(ServerUpdatePlayerListMessage message) {
	   if (message.getAction() == ServerUpdatePlayerListMessage.Action.ADD) {
		  essentialsVC.getNetworkPlayers().add(message.getPlayer());
	   } else if (message.getAction() == ServerUpdatePlayerListMessage.Action.REMOVE) {
		  essentialsVC.getNetworkPlayers().remove(message.getPlayer());
	   }
    }

    @MessageHandler(TeleportStateMessage.class)
    public void handleTeleportStateMessage(TeleportStateMessage message) {
		EssPlayer essPlayer = essentialsVC.getEssPlayer().values().stream().filter(it -> it.getPlayer().getUniqueId().equals(message.getPlayer())).findFirst().orElse(null);
		if (essPlayer == null) return;

		if (!essPlayer.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown"))
			essPlayer.setTeleporting(message.getState() == TeleportStateMessage.State.TELEPORTING);
	}

    @MessageHandler(UpdateBackMessage.class)
    public void handleUpdateBackMessage(UpdateBackMessage message) {
	   Player player = essentialsVC.getServer().getPlayer(message.getPlayer());
	   if (player == null) return;
	   essentialsVC.getMessenger().sendMessage(player, new UpdateBackMessage(player.getUniqueId(), toLocationObject(player.getLocation())));
    }

    private Location toBukkitLocation(LocationObject locationObject) {
	   return new Location(essentialsVC.getServer().getWorld(locationObject.getWorld()),
			 locationObject.getX(), locationObject.getY(), locationObject.getZ(), locationObject.getYaw(), locationObject.getPitch());
    }

    private LocationObject toLocationObject(Location location) {
	   return new LocationObject(location.getWorld().getName(),
			 location.getX(), location.getY(), location.getZ(),
			 location.getYaw(), location.getPitch());
    }
}
