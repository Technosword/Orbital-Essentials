package it.thedarksword.essentialsvc.commands;

import com.google.common.collect.ImmutableList;
import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import it.thedarksword.essentialsvc.player.EssPlayer;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameModeCommand extends AbstractCommand {

    public GameModeCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "gamemode");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length == 0) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
            essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.GAMEMODE));
            return;
        }
        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(player.getName());
        String gameMode = args[0].toLowerCase();

        switch (gameMode) {
            case "survival":
            case "s":
            case "0":
                essPlayer.setGameMode(GameMode.SURVIVAL);
                break;
            case "creative":
            case "c":
            case "1":
                essPlayer.setGameMode(GameMode.CREATIVE);
                break;
            case "spectator":
            case "sp":
            case "3":
                essPlayer.setGameMode(GameMode.SPECTATOR);
                break;
            case "adventure":
            case "a":
            case "2":
                essPlayer.setGameMode(GameMode.ADVENTURE);
                break;
            default:
                essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
                essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.GAMEMODE));
                return;
        }

        player.setGameMode(essPlayer.getGameMode());
        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.GAMEMODE, new String[][]{{"{gamemode}", player.getGameMode().name().toLowerCase()}}));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ImmutableList.of("creative", "survival", "spectator", "adventure");
    }
}
