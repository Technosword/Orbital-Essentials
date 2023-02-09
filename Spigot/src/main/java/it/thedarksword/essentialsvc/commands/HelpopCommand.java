package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpopMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import org.bukkit.entity.Player;

public class HelpopCommand extends AbstractCommand {

    public HelpopCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "helpop");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.HELPOP));
            return;
        }

        essentialsVC.getMessenger().sendMessage(player, new ClientHelpopMessage(player.getName(), essentialsVC.chatBuilder(args)));
    }
}
