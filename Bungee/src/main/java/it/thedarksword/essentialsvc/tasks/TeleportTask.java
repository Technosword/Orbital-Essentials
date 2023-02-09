package it.thedarksword.essentialsvc.tasks;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TeleportTask implements Runnable {

    private final EssentialsVC essentialsVC;
    private final List<UserAccount> teleports = new ArrayList<>();

    @Override
    public void run() {
	   teleports.removeIf(essPlayer -> {
		  if (essPlayer.getPlayer() == null || essPlayer.getTeleporting() == null) return true;

		  if (essPlayer.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown")) {
			 essPlayer.getTeleporting().getExecute().accept(essPlayer);
			 essPlayer.setTeleporting(null);
			 return true;
		  }

		  if (essPlayer.getTeleporting().getSeconds() == 3) {
			 essPlayer.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().TELEPORTING.getReplaced("{seconds}", "3"));
		  } else if (essPlayer.getTeleporting().getSeconds() == 2) {
			 essPlayer.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().TELEPORTING.getReplaced("{seconds}", "2"));
		  } else if (essPlayer.getTeleporting().getSeconds() == 1) {
			 essPlayer.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().TELEPORTING.getReplaced("{seconds}", "1"));
		  } else {
			 //essPlayer.getPlayer().teleport(essPlayer.getTeleporting().getLocation());
			 essPlayer.getTeleporting().getExecute().accept(essPlayer);
			 essPlayer.setTeleporting(null);
			 return true;
		  }
		  essPlayer.getTeleporting().decrementSeconds();
		  return false;
	   });
    }

    public void addTeleport(UserAccount userAccount, LocationObject location, Consumer<UserAccount> execute) {
	   userAccount.setTeleporting(new UserAccount.Teleporting(location, null, 3, execute));
	   teleports.add(userAccount);
    }

    public void addTeleport(UserAccount userAccount, UserAccount target, Consumer<UserAccount> execute) {
	   userAccount.setTeleporting(new UserAccount.Teleporting(null, target, 3, execute));
	   teleports.add(userAccount);
    }
}
