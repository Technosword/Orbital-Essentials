package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.player.EssPlayer;
import org.bukkit.entity.Player;

public class TPDenyCommand extends AbstractCommand {

    public TPDenyCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "tpdeny");
    }

    @Override
    public void execute(Player player, String[] args) {
        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(player.getName());

        if(essPlayer.getTpaRequest() == null) {
            essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.NO_TPA_TO_ACCEPT));
            return;
        }
        essPlayer.setTpaRequest(null);
        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.TP_DENY));
    }
}
