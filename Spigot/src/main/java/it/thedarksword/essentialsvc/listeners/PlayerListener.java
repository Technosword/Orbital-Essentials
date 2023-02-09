package it.thedarksword.essentialsvc.listeners;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientRequestPlayerListMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientSpawnMessage;
import it.thedarksword.essentialsvc.messaging.client.ClientUpdateBackMessage;
import it.thedarksword.essentialsvc.messaging.common.TeleportStateMessage;
import it.thedarksword.essentialsvc.messaging.server.ServerContainsPluginMessage;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.player.EssPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

import java.util.function.Consumer;

public class PlayerListener implements Listener {

    private final EssentialsVC essentialsVC;
    private Consumer<Player> firstLogin;
    private boolean firstJoin = true;

    public PlayerListener(EssentialsVC essentialsVC) {
        this.essentialsVC = essentialsVC;
        firstLogin = player -> {
            essentialsVC.getMessenger().sendMessage(player, new ClientRequestPlayerListMessage());
        };
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(firstJoin) {
            essentialsVC.getServer().getScheduler().runTaskLater(essentialsVC, () -> {
                firstLogin.accept(event.getPlayer());
                firstLogin = null;
            }, 20);
            firstJoin = false;
        }
        Consumer<Player> consumer = essentialsVC.getMessageOnLogin().get(event.getPlayer().getName());
        if(consumer != null) consumer.accept(event.getPlayer());
        essentialsVC.getEssPlayer().put(event.getPlayer().getName(), new EssPlayer(event.getPlayer()));
        event.setJoinMessage("");

        essentialsVC.getServer().getScheduler().runTaskLater(essentialsVC, () -> {
            essentialsVC.getMessenger().sendMessage(event.getPlayer(), new ServerContainsPluginMessage(true));

            // teleport player to spawn on first join
            /**
            if (!event.getPlayer().hasPlayedBefore()) {
                Player player = event.getPlayer();

                essentialsVC.getMessenger().sendMessage(player, new ClientSpawnMessage(player.getName(),
                        new LocationObject(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                                player.getLocation().getYaw(), player.getLocation().getPitch()),
                        ClientSpawnMessage.SpawnType.FORCE));
            }
             **/
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        essentialsVC.getEssPlayer().remove(event.getPlayer().getName());
        event.setQuitMessage("");
    }

    @EventHandler
    public void onReSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        essentialsVC.getMessenger().sendMessage(player, new ClientSpawnMessage(player.getName(),
                new LocationObject(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
                        player.getLocation().getYaw(), player.getLocation().getPitch()),
                ClientSpawnMessage.SpawnType.FORCE));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if(from.getWorld() == null || to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ())) return;

        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getPlayer().getName());
        if(essPlayer != null && essPlayer.isTeleporting()) {
            essentialsVC.getMessenger().sendMessage(essPlayer.getPlayer(), new TeleportStateMessage(essPlayer.getPlayer().getUniqueId(),
                    TeleportStateMessage.State.CANCEL));
            essPlayer.setTeleporting(false);
            essPlayer.setTeleport(null);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location location = event.getFrom();
        Location to = event.getTo();
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE || location.getWorld() == null || to == null || (location.getBlockX() == to.getBlockX() &&
                location.getBlockY() == to.getBlockY() && location.getBlockZ() == to.getBlockZ())) return;

        /*System.out.println("Teleport to x=" + location.getX() + " - y=" + location.getY()  + " - z=" + location.getZ());
        System.out.println("Yaw=" + location.getYaw() + " - Pitch=" + location.getPitch());
        System.out.println("Cause=" + event.getCause().name());*/
        //EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getPlayer().getName());
        essentialsVC.getMessenger().sendMessage(event.getPlayer(), new ClientUpdateBackMessage(event.getPlayer().getUniqueId(),
                new LocationObject(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch())));
        //essPlayer.setLastLocation(event.getFrom());
    }

    @EventHandler
    public void onPlayerHit(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getEntity().getName());
            if(essPlayer == null || essPlayer.isGod() || essPlayer.isTeleporting()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCancelTeleport(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getDamager() instanceof Player) {
            EssPlayer essDamager = essentialsVC.getEssPlayer().get(event.getDamager().getName());
            EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getEntity().getName());
            if(essDamager == null || essDamager.isTeleporting() || essPlayer == null || essPlayer.isGod() || essPlayer.isTeleporting()) {
                /*essentialsVC.getMessenger().sendMessage(essDamager.getPlayer(), new TeleportStateMessage(essDamager.getPlayer().getName(),
                        TeleportStateMessage.State.CANCEL));
                essDamager.setTeleporting(false);
                essDamager.setTeleport(null);*/
                event.setCancelled(true);
            }
        } else {
            EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getEntity().getName());
            if(essPlayer == null || essPlayer.isGod() || essPlayer.isTeleporting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getPlayer().getName());
        if(essPlayer != null && essPlayer.isTeleporting()) event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getPlayer().getName());
        if(essPlayer == null || essPlayer.isTeleporting()) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        EssPlayer essPlayer = essentialsVC.getEssPlayer().get(event.getEntity().getName());
        if(essPlayer == null || essPlayer.isTeleporting()) event.setCancelled(true);
    }

}
