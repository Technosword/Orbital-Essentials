package it.thedarksword.essentialsvc.commands;

import com.google.common.collect.ImmutableList;
import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.player.EssPlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class FlyCommand extends AbstractCommand {

    public FlyCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "fly");
    }

    @Override
    public void execute(Player player, String[] args) {
        EssPlayer essPlayer;
        boolean flying;

        if(args.length == 0) {
            essPlayer = essentialsVC.getEssPlayer().get(player.getName());
            flying = !essPlayer.isFlying();
        } else {
            if(args[0].equalsIgnoreCase("on")) {
                essPlayer = essentialsVC.getEssPlayer().get(player.getName());
                flying = true;
            } else if(args[0].equalsIgnoreCase("off")) {
                essPlayer = essentialsVC.getEssPlayer().get(player.getName());
                flying = false;
            } else {
                essPlayer = essentialsVC.getEssPlayer().get(args[0]);
                if(essPlayer == null) {
                    essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.PLAYER_NOT_FOUND));
                    return;
                } else {
                    if (args.length > 1) {
                        flying = args[1].equalsIgnoreCase("on");
                    } else {
                        flying = !essPlayer.isFlying();
                    }
                }
            }
        }

        essPlayer.setFlying(flying);
        player.setAllowFlight(flying);
        player.setFlying(flying);
        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.FLY,
                new String[][]{{"{status}", essPlayer.isFlying() ? "on" : "off"}}));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> value = essentialsVC.getServer().getOnlinePlayers().parallelStream()
                    .map(HumanEntity::getName).filter(name -> name.startsWith(args[0])).collect(Collectors.toList());
            value.add("on");
            value.add("off");
            return value;
        }
        return ImmutableList.of("on", "off");
    }
}
