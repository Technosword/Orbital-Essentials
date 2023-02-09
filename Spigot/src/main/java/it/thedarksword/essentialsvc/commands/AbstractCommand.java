package it.thedarksword.essentialsvc.commands;

import com.google.common.collect.ImmutableList;
import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabExecutor {

    protected final EssentialsVC essentialsVC;
    private final String permission;

    public AbstractCommand(EssentialsVC essentialsVC, String permission) {
        this.essentialsVC = essentialsVC;
        this.permission = "essentialsvc." + permission;
    }

    public abstract void execute(Player player, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can be executed only by a player");
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission(permission)) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INSUFFICIENT_PERMISSION));
            return true;
        }

        execute(player, args);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) return onTabComplete((Player) sender, command, label, args);
        return ImmutableList.of();
    }

    @Nullable
    public List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ImmutableList.of();
    }
}
