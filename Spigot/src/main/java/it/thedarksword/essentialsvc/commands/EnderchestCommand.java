package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class EnderchestCommand extends AbstractCommand {

    public EnderchestCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "enderchest");
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target;
        if(args.length == 0) {
            target = player;
        } else {
            if(!player.hasPermission("essentialsvc.enderchest.others")) {
                essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INSUFFICIENT_PERMISSION));
                return;
            }
            target = essentialsVC.getServer().getPlayer(args[0]);
            if(target == null) {
                essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.PLAYER_NOT_FOUND));
                return;
            }
        }

        player.openInventory(target.getEnderChest());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return essentialsVC.getServer().getOnlinePlayers().parallelStream()
                .map(HumanEntity::getName).filter(name -> name.startsWith(args[0])).collect(Collectors.toList());
    }
}
