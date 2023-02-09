package it.thedarksword.essentialsvc.listeners;

import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onInit(WorldInitEvent event) {
        event.getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }
}
