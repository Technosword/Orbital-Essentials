package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EssentialsVCCommand implements CommandExecutor {

    private final EssentialsVC essentialsVC;

    public EssentialsVCCommand(EssentialsVC essentialsVC) {
        this.essentialsVC = essentialsVC;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return true;
        essentialsVC.getMessenger().sendMessage((Player) sender, new ClientHelpMessage(sender.getName(), HelpType.ALL));
        return true;
    }
}
