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

public class GodCommand extends AbstractCommand {

    public GodCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "god");
    }

    @Override
    public void execute(Player player, String[] args) {
        EssPlayer essPlayer;
        boolean god;

        if(args.length == 0) {
            essPlayer = essentialsVC.getEssPlayer().get(player.getName());
            god = !essPlayer.isGod();
        } else {
            if(args[0].equalsIgnoreCase("on")) {
                essPlayer = essentialsVC.getEssPlayer().get(player.getName());
                god = true;
            } else if(args[0].equalsIgnoreCase("off")) {
                essPlayer = essentialsVC.getEssPlayer().get(player.getName());
                god = false;
            } else {
                essPlayer = essentialsVC.getEssPlayer().get(args[0]);
                if(essPlayer == null) {
                    essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.PLAYER_NOT_FOUND));
                    return;
                } else {
                    if (args.length > 1) {
                        god = args[1].equalsIgnoreCase("on");
                    } else {
                        god = !essPlayer.isGod();
                    }
                }
            }
        }

        essPlayer.setGod(god);
        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.GOD,
                new String[][]{{"{status}", essPlayer.isGod() ? "on" : "off"}}));
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
