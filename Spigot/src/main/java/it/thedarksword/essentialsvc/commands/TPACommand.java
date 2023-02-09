package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientTeleportToPlayerMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.objets.Tuple;
import it.thedarksword.essentialsvc.player.EssPlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class TPACommand extends AbstractCommand {

    public TPACommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "tpa");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0 || args[0].equalsIgnoreCase(player.getName())) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.TPA));
            return;
        }

        Player target = essentialsVC.getServer().getPlayer(args[0]);
        if(target == null) {
            essentialsVC.getMessenger().sendMessage(player, new ClientTeleportToPlayerMessage(player.getName(), args[0],
                    new LocationObject(player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                            player.getLocation().getYaw(), player.getLocation().getPitch()),
                    ClientTeleportToPlayerMessage.Cause.TPA));
            //essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.PLAYER_NOT_FOUND));
            return;
        }

        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(target.getName());

        essPlayer.setTpaRequest(new Tuple<>(player.getName(),
                System.currentTimeMillis() + 120_000));

        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.TPA_SENT, new String[][]{{"{player}", target.getName()}}));
        essentialsVC.getMessenger().sendMessage(target, new ClientSendConfigMessage(target.getName(), ConfigMessage.TPA_REQUEST, new String[][]{{"{player}", player.getName()}}));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return essentialsVC.getNetworkPlayers().parallelStream()
                .filter(name -> name.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
    }
}
