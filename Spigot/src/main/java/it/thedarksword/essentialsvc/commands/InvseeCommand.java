package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import org.bukkit.entity.Player;

public class InvseeCommand extends AbstractCommand {

    public InvseeCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "invsee");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.INVSEE));
            return;
        }
        if(player.getName().equalsIgnoreCase(args[0])) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.CANT_INVSEE));
            return;
        }
        Player target = essentialsVC.getServer().getPlayer(args[0]);
        if(target == null) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.PLAYER_NOT_FOUND));
            return;
        }

        player.openInventory(target.getInventory());
    }
}
