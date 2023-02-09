package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientMsgMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import org.bukkit.entity.Player;

public class ReplyCommand extends AbstractCommand {

    public ReplyCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "reply");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.REPLY));
            return;
        }
        String message = essentialsVC.chatBuilder(args);

        essentialsVC.getMessenger().sendMessage(player, new ClientMsgMessage(player.getUniqueId(), "", message));
    }
}
