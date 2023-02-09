package it.thedarksword.essentialsvc.commands;

import com.google.common.collect.ImmutableList;
import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientHomeMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import it.thedarksword.essentialsvc.objets.LocationObject;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DelHomeCommand extends AbstractCommand {

    public DelHomeCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "delhome");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.DELHOME));
            return;
        }

        if(args.length > 1) {
            essentialsVC.getMessenger().sendMessage(player, new ClientHomeMessage(args[1], args[0],  player.getName(), new LocationObject(),
                    ClientHomeMessage.HomeOperation.REMOVE));
            return;
        }

        essentialsVC.getMessenger().sendMessage(player, new ClientHomeMessage(player.getName(), args[0],  player.getName(), new LocationObject(),
                ClientHomeMessage.HomeOperation.REMOVE));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 2)
            return essentialsVC.getNetworkPlayers().parallelStream()
                    .filter(name -> name.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        return ImmutableList.of();
    }
}
