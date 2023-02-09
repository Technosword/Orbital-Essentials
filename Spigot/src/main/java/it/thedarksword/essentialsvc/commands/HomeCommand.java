package it.thedarksword.essentialsvc.commands;

import com.google.common.collect.ImmutableList;
import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHomeMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class HomeCommand extends AbstractCommand {

    public HomeCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "home");
    }

    @Override
    public void execute(Player player, String[] args) {
        String homeName;

        if(args.length == 0) {
            homeName = "home";
        } else {
            homeName = args[0];

            if(args.length > 1) {
                essentialsVC.getMessenger().sendMessage(player, new ClientHomeMessage(player.getName(), homeName, args[1],
                        new LocationObject(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                                player.getLocation().getYaw(), player.getLocation().getPitch()),
                        ClientHomeMessage.HomeOperation.LOAD));
                return;
            }
        }

        essentialsVC.getMessenger().sendMessage(player, new ClientHomeMessage(player.getName(), homeName, player.getName(),
                new LocationObject(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                        player.getLocation().getYaw(), player.getLocation().getPitch()),
                ClientHomeMessage.HomeOperation.LOAD));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 2)
            return essentialsVC.getNetworkPlayers().parallelStream()
                .filter(name -> name.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        return ImmutableList.of();
    }
}
