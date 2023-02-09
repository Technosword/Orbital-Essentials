package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHomeListMessage;
import org.bukkit.entity.Player;

public class HomeListCommand extends AbstractCommand {

    public HomeListCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "homelist");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length != 0 && player.hasPermission("essentialsvc.homelist.others")) {
            essentialsVC.getMessenger().sendMessage(player, new ClientHomeListMessage(player.getName(), args[0]));
            return;
        }
        essentialsVC.getMessenger().sendMessage(player, new ClientHomeListMessage(player.getName(), player.getName()));
    }
}
