package it.thedarksword.essentialsvc.tasks;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.player.EssPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TeleportTask implements Runnable {

    private final EssentialsVC essentialsVC;
    private final List<EssPlayer> teleports = new ArrayList<>();

    @Override
    public void run() {
	   teleports.removeIf(essPlayer -> {
		  if (essPlayer.getPlayer() == null || essPlayer.getTeleport() == null || !essPlayer.isTeleporting()) return true;

		  if (essPlayer.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown")) {
			 essPlayer.getTeleport().getExecute().accept(essPlayer);

			 if (essPlayer.getTeleport().getMessage() != null)
				essentialsVC.getMessenger().sendMessage(essPlayer.getPlayer(), essPlayer.getTeleport().getMessage());

			 essPlayer.setTeleport(null);
			 essPlayer.setTeleporting(false);
			 return true;
		  }

		  if (essPlayer.getTeleport().getSeconds() == 3) {
			 essentialsVC.getMessenger().sendMessage(essPlayer.getPlayer(), new ClientSendConfigMessage(essPlayer.getPlayer().getName(), ConfigMessage.TELEPORTING,
				    new String[][]{{"{seconds}", "3"}}));
		  } else if (essPlayer.getTeleport().getSeconds() == 2) {
			 essentialsVC.getMessenger().sendMessage(essPlayer.getPlayer(), new ClientSendConfigMessage(essPlayer.getPlayer().getName(), ConfigMessage.TELEPORTING,
				    new String[][]{{"{seconds}", "2"}}));
		  } else if (essPlayer.getTeleport().getSeconds() == 1) {
			 essentialsVC.getMessenger().sendMessage(essPlayer.getPlayer(), new ClientSendConfigMessage(essPlayer.getPlayer().getName(), ConfigMessage.TELEPORTING,
				    new String[][]{{"{seconds}", "1"}}));
		  } else {
			 //essPlayer.getPlayer().teleport(essPlayer.getTeleporting().getLocation());
			 essPlayer.getTeleport().getExecute().accept(essPlayer);
			 if (essPlayer.getTeleport().getMessage() != null)
				essentialsVC.getMessenger().sendMessage(essPlayer.getPlayer(), essPlayer.getTeleport().getMessage());
			 essPlayer.setTeleport(null);
			 essPlayer.setTeleporting(false);
			 return true;
		  }

		  essPlayer.getTeleport().decrementSeconds();
		  return false;
	   });
    }

    public void addTeleport(EssPlayer essPlayer, Location location, ClientSendConfigMessage message, Consumer<EssPlayer> execute) {
	   essPlayer.setTeleport(new EssPlayer.Teleport(location, 3, execute, message));
	   essPlayer.setTeleporting(true);
	   teleports.add(essPlayer);
    }
}
