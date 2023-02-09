package it.thedarksword.essentialsvc.commands;

import com.google.common.collect.ImmutableList;
import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientHelpMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientMsgMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.objets.HelpType;
import org.bukkit.command.Command;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MessageCommand extends AbstractCommand {

    public MessageCommand(EssentialsVC essentialsVC) {
	   super(essentialsVC, "message");
    }

    @Override
    public void execute(Player player, String[] args) {
	   if (args.length < 2) {
		  essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.INVALID_ARGUMENTS));
		  essentialsVC.getMessenger().sendMessage(player, new ClientHelpMessage(player.getName(), HelpType.MESSAGE));
		  return;
	   }

	   String target = args[0];
	   if (target.equalsIgnoreCase(player.getName())) return;
	   String message = essentialsVC.chatBuilder(args, 1);

	   essentialsVC.getMessenger().sendMessage(player, new ClientMsgMessage(player.getUniqueId(), target, message));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
	   if (args.length == 1)
		  return essentialsVC.getNetworkPlayers().parallelStream()
				.filter(name -> name.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
	   return ImmutableList.of();
    }
}
