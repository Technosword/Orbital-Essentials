package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import org.bukkit.entity.Player;

public class NicknameCommand extends AbstractCommand {

    public NicknameCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "nickname");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.NICKNAME));
            return;
        }

        player.setDisplayName(args[0]);
        player.setPlayerListName(args[0]);

        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.NICKNAME));
    }
}
