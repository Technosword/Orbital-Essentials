package it.thedarksword.essentialsvc.listeners;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.server.ServerUpdatePlayerListMessage;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final EssentialsVC essentialsVC;

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if (essentialsVC.getUserRepository().get(event.getPlayer().getUniqueId()) == null) {
            essentialsVC.getUserRepository().create(new UserAccount(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
        }
        essentialsVC.getUserRepository().get(event.getPlayer().getUniqueId()).setPlayer(event.getPlayer());

        ServerUpdatePlayerListMessage message = new ServerUpdatePlayerListMessage(event.getPlayer().getName(), ServerUpdatePlayerListMessage.Action.ADD);
        for(String server : essentialsVC.getProxy().getServers().keySet()) {
            essentialsVC.getMessenger().sendMessage(message, server);
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        essentialsVC.getUserRepository().get(event.getPlayer().getUniqueId()).setPlayer(null);

        ServerUpdatePlayerListMessage message = new ServerUpdatePlayerListMessage(event.getPlayer().getName(), ServerUpdatePlayerListMessage.Action.REMOVE);
        for(String server : essentialsVC.getProxy().getServers().keySet()) {
            essentialsVC.getMessenger().sendMessage(message, server);
        }
    }

}
