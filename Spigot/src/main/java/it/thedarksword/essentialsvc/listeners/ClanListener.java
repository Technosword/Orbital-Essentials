package it.thedarksword.essentialsvc.listeners;

import it.thedarksword.essentialsvc.chat.Chat;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.TagChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClanListener implements Listener {

    private final Chat chat;

    public ClanListener(Chat chat) {
        this.chat = chat;
    }

    @EventHandler
    public void onDisband(DisbandClanEvent event) {
        if (chat.clanTagChat == null) return;

        chat.clanTagChat.cache.keySet().stream()
                .filter(it -> chat.clanTagChat.cache.get(it).getKey().equals(event.getClan().getName()))
                .forEach(target -> {
                    chat.clanTagChat.cache.remove(target);
        });
    }

    @EventHandler
    public void onTagChange(TagChangeEvent event) {
        if (chat.clanTagChat == null) return;

        chat.clanTagChat.cache.keySet().stream()
                .filter(it -> chat.clanTagChat.cache.get(it).getKey().equals(event.getClan().getName()))
                .forEach(target -> {
                    chat.clanTagChat.cache.remove(target);
                });
    }

}
