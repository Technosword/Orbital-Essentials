package it.thedarksword.essentialsvc.messaging;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.data.domain.HomeData;
import it.thedarksword.essentialsvc.data.domain.SpawnData;
import it.thedarksword.essentialsvc.handler.MessageHandler;
import it.thedarksword.essentialsvc.handler.MessageListener;
import it.thedarksword.essentialsvc.messaging.client.*;
import it.thedarksword.essentialsvc.messaging.common.TeleportStateMessage;
import it.thedarksword.essentialsvc.messaging.common.UpdateBackMessage;
import it.thedarksword.essentialsvc.messaging.server.*;
import it.thedarksword.essentialsvc.objets.LocationObject;
import it.thedarksword.essentialsvc.objets.Tuple;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import it.thedarksword.essentialsvc.utils.ServerLocationObject;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BungeeMessageListener implements MessageListener {

    private static final String PERMISSION_HOMES = "essentialsvc.home.";

    private final EssentialsVC essentialsVC;

    @MessageHandler(ServerContainsPluginMessage.class)
	public void handleContainsPluginMessage(ServerContainsPluginMessage message, String server) {
    	if (message.isContainsPlugin()) {
    		essentialsVC.getServers().add(server);
    		return;
		}

    	essentialsVC.getServers().remove(server);
	}

    @MessageHandler(ClientSendConfigMessage.class)
    public void handleClientSendConfigMessage(ClientSendConfigMessage message, String server) {
	   ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
	   if (player != null) {
		  TextComponent component;
		  if (message.getReplaces().isEmpty()) {
			 component = essentialsVC.getConfigValue().getMessage().byName(message.getPath()).get();
		  } else {
			 List<String> from = new LinkedList<>();
			 List<String> to = new LinkedList<>();
			 for (Map.Entry<String, String> entry : message.getReplaces().entrySet()) {
				from.add(entry.getKey());
				to.add(entry.getValue());
			 }
			 component = essentialsVC.getConfigValue().getMessage().byName(message.getPath())
				    .getReplaced(from.toArray(new String[0]), to.toArray(new String[0]));
		  }
		  player.sendMessage(component);
	   }
    }

    @MessageHandler(ClientHomeListMessage.class)
    public void handleClientHomeListMessage(ClientHomeListMessage message, String server) {
	   ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
	   ProxiedPlayer target = essentialsVC.getProxy().getPlayer(message.getWho());
	   if (player == null || target == null) return;

	   List<HomeData> homes = essentialsVC.getHomeRepository().getData().stream().filter(home -> home.getPlayer().equals(target.getUniqueId())).collect(Collectors.toList());
		StringBuilder builder = new StringBuilder();
	   if (!homes.isEmpty()) {
		   builder.append(homes.get(0).getName());
		   for (int i = 1; i < homes.size(); i++) {
			   builder.append(", ").append(homes.get(i).getName());
		   }
	   }

		player.sendMessage(essentialsVC.getConfigValue().getMessage().HOME_LIST.getReplaced("{homes}", builder.toString()));
    }

    @MessageHandler(ClientHomeMessage.class)
    public void handleClientHomeMessage(ClientHomeMessage message, String server) {
		ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
	   if (player == null) return;
	   String homeName = message.getHome();

	   switch (message.getOperation()) {
		   case ADD: {
			   int maxHomes = essentialsVC.getConfigValue().getHomes().getMaxHomes("default");

			   if (player.hasPermission(PERMISSION_HOMES + "*")) {
				   maxHomes = Integer.MAX_VALUE;
			   } else {
				   for (Map.Entry<String, Integer> entry : essentialsVC.getConfigValue().getHomes().entrySet()) {
					   if (player.hasPermission(PERMISSION_HOMES + entry.getKey()) && maxHomes < entry.getValue()) {
						   maxHomes = entry.getValue();
					   }
				   }
			   }

			   final int finalMaxHomes = maxHomes;
			   List<HomeData> homes = essentialsVC.getHomeRepository().getData().stream().filter(home -> home.getPlayer().equals(player.getUniqueId())).filter(home -> !home.getName().equalsIgnoreCase(homeName)).collect(Collectors.toList());
			   if (homes.size() >= finalMaxHomes) {
				   player.sendMessage(essentialsVC.getConfigValue().getMessage().MAX_HOMES.get());
				   return;
			   }

			   essentialsVC.getMessenger().sendMessage(
					   new ServerHomeLocationMessage(player.getName(), homeName),
					   player.getServer().getInfo().getName());
			   break;
		   }
		   case REMOVE: {
		   	   UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());
			   Optional<HomeData> home = essentialsVC.getHomeRepository().get(homeName, player.getUniqueId());
			   if (!home.isPresent()) {
				   player.sendMessage(essentialsVC.getConfigValue().getMessage().NO_HOME_NAME.get());
				   return;
			   }

			   essentialsVC.getHomeRepository().delete(home.get());
			   player.sendMessage(essentialsVC.getConfigValue().getMessage().HOME_REMOVED.get());
			   break;
		   }
		   case LOAD: {
			   ProxiedPlayer target = essentialsVC.getProxy().getPlayer(message.getWho());
			   if (target == null) return;

			   Optional<HomeData> home = essentialsVC.getHomeRepository().get(homeName, target.getUniqueId());
			   if (!home.isPresent()) {
				   player.sendMessage(essentialsVC.getConfigValue().getMessage().NO_HOME_NAME.get());
				   return;
			   }

			   ServerLocationObject location = home.get().getLocationObject();
			   UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());

			   if (!userAccount.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown"))
				   essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(player.getUniqueId(), TeleportStateMessage.State.TELEPORTING), server);

			   essentialsVC.getTeleportTask().addTeleport(userAccount, location, ess -> {
				   ess.setLastLocation(new ServerLocationObject(server, message.getLocation()));

				   if (!ess.getPlayer().getServer().getInfo().getName().equals(location.getServer()))
					   ess.getPlayer().connect(essentialsVC.getProxy().getServerInfo(location.getServer()));
				   else
					   essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(ess.getPlayer().getUniqueId(), TeleportStateMessage.State.CANCEL),
							   ess.getPlayer().getServer().getInfo().getName());
				   essentialsVC.getMessenger().sendMessage(
						   new ServerTeleportToLocationMessage(ess.getPlayer().getName(), location, ServerTeleportToLocationMessage.TeleportCause.HOME), location.getServer());

				   player.sendMessage(essentialsVC.getConfigValue().getMessage().HOME.get());
			   });
			   break;
		   }
	   }
    }

    @MessageHandler(ClientHomeSetMessage.class)
    public void handleClientHomeSetMessage(ClientHomeSetMessage message, String server) {
		ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
		if (player != null) {
			UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());
			Optional<HomeData> optional = essentialsVC.getHomeRepository().get(message.getHomeName(), player.getUniqueId());
			ServerLocationObject location = new ServerLocationObject(server, message.getLocation().getWorld(), message.getLocation().getX(), message.getLocation().getY(), message.getLocation().getZ(), message.getLocation().getYaw(), message.getLocation().getPitch());
			if (optional.isPresent()) {
				HomeData home = optional.get();
				home.setServer(server);
				home.setLocationObject(location);

				essentialsVC.getHomeRepository().edit(home);
			} else essentialsVC.getHomeRepository().create(new HomeData(essentialsVC.getHomeRepository().getData().size() + 1, message.getHomeName(), server, player.getUniqueId(), location));

			player.sendMessage(essentialsVC.getConfigValue().getMessage().HOME_SET.get());
		}
	}

    @MessageHandler(ClientHelpopMessage.class)
    public void handleClientHelpopMessage(ClientHelpopMessage message, String server) {
	   ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());

	   TextComponent helpop = essentialsVC.getConfigValue().getMessage().HELPOP.getReplaced("{message}", message.getMessage());
	   essentialsVC.getProxy().getScheduler().runAsync(essentialsVC, () -> {
		  for (ProxiedPlayer proxiedPlayer : essentialsVC.getProxy().getPlayers()) {
			 if (proxiedPlayer.hasPermission("essentialscv.helpop.receive"))
				proxiedPlayer.sendMessage(helpop);
		  }
	   });
	   if (player != null) player.sendMessage(helpop);
    }

    @MessageHandler(ClientMsgMessage.class)
    public void handleClientMsgMessage(ClientMsgMessage message, String server) {
	   UserAccount userAccount = essentialsVC.getUserRepository().get(message.getPlayer());
	   if (message.getTarget().isEmpty()) {
		  if (userAccount == null || userAccount.getLastMessaged() == null) {
			 userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
			 return;
		  }
		  UserAccount essTarget = essentialsVC.getUserRepository().get(userAccount.getLastMessaged());
		  if (essTarget == null || !essentialsVC.getServers().contains(essTarget.getPlayer().getServer().getInfo().getName())) {
			 userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
			 return;
		  }
		  essTarget.setLastMessaged(userAccount.getPlayer().getUniqueId());

		  userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().MESSAGE_FROM
				.getReplaced(new String[]{"{player-from}", "{player-to}", "{message}"}, new String[]{userAccount.getPlayer().getName(), essTarget.getPlayer().getName(), message.getMessage()}));
		  essTarget.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().MESSAGE_TO
				.getReplaced(new String[]{"{player-from}", "{player-to}", "{message}"}, new String[]{userAccount.getPlayer().getName(), essTarget.getPlayer().getName(), message.getMessage()}));
		  return;
	   }
	   ProxiedPlayer target = essentialsVC.getProxy().getPlayer(message.getTarget());
	   if (target == null) {
		   if (userAccount != null) userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
		   return;
	   }

	   UserAccount essTarget = essentialsVC.getUserRepository().get(target.getUniqueId());
	   if (userAccount == null) return;
	   if (essTarget == null || !essentialsVC.getServers().contains(essTarget.getPlayer().getServer().getInfo().getName())) {
		  userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
		  return;
	   }

	   userAccount.setLastMessaged(essTarget.getPlayer().getUniqueId());
	   essTarget.setLastMessaged(userAccount.getPlayer().getUniqueId());

	   userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().MESSAGE_FROM
			 .getReplaced(new String[]{"{player-from}", "{player-to}", "{message}"}, new String[]{userAccount.getPlayer().getName(), essTarget.getPlayer().getName(), message.getMessage()}));
	   essTarget.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().MESSAGE_TO
			 .getReplaced(new String[]{"{player-from}", "{player-to}", "{message}"}, new String[]{userAccount.getPlayer().getName(), essTarget.getPlayer().getName(), message.getMessage()}));
    }

    @MessageHandler(ClientSpawnMessage.class)
    public void handleClientSpawnMessage(ClientSpawnMessage message, String server) {
		ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
		if (player == null) return;

		if (message.getType() == ClientSpawnMessage.SpawnType.SAVE) {
			SpawnData spawn = essentialsVC.getSpawnRepository().get(server);
			ServerLocationObject location = new ServerLocationObject(server, message.getLocation().getWorld(), message.getLocation().getX(), message.getLocation().getY(), message.getLocation().getZ(), message.getLocation().getYaw(), message.getLocation().getPitch());
			if (spawn != null) {
				spawn.setLocationObject(location);
				essentialsVC.getSpawnRepository().edit(spawn);
			} else essentialsVC.getSpawnRepository().create(new SpawnData(server, location));

			player.sendMessage(essentialsVC.getConfigValue().getMessage().SPAWN_SET.get());
		} else if (message.getType() == ClientSpawnMessage.SpawnType.LOAD) {
			SpawnData spawn;
			if (essentialsVC.getSpawnRepository().getData().size() == 1) spawn = essentialsVC.getSpawnRepository().getData().get(0);
			else spawn = essentialsVC.getSpawnRepository().get(server);
			if (spawn == null) {
				player.sendMessage(essentialsVC.getConfigValue().getMessage().SPAWN_NOT_SET.get());
				return;
			}
			ServerLocationObject location = spawn.getLocationObject();
			UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());

			if (!userAccount.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown"))
				essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(player.getUniqueId(), TeleportStateMessage.State.TELEPORTING),
						server);

			essentialsVC.getTeleportTask().addTeleport(userAccount, location, ess -> {
				ess.setLastLocation(new ServerLocationObject(server, message.getLocation()));
				if (!ess.getPlayer().getServer().getInfo().getName().equals(location.getServer()))
					ess.getPlayer().connect(essentialsVC.getProxy().getServerInfo(location.getServer()));
				else
					essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(ess.getPlayer().getUniqueId(), TeleportStateMessage.State.CANCEL),
							ess.getPlayer().getServer().getInfo().getName());

				essentialsVC.getMessenger().sendMessage(
						new ServerTeleportToLocationMessage(ess.getPlayer().getName(), location, ServerTeleportToLocationMessage.TeleportCause.SPAWN), location.getServer());
				player.sendMessage(essentialsVC.getConfigValue().getMessage().SPAWN.get());
			});
		} else {
			SpawnData spawn;
			if (essentialsVC.getSpawnRepository().getData().size() == 1) spawn = essentialsVC.getSpawnRepository().getData().get(0);
			else spawn = essentialsVC.getSpawnRepository().get(server);
			if (spawn == null) return;

			ServerLocationObject location = spawn.getLocationObject();
			UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());
			userAccount.setLastLocation(new ServerLocationObject(server, message.getLocation()));
			if (!player.getServer().getInfo().getName().equals(location.getServer()))
				player.connect(essentialsVC.getProxy().getServerInfo(location.getServer()));

			essentialsVC.getMessenger().sendMessage(
					new ServerTeleportToLocationMessage(player.getName(), location, ServerTeleportToLocationMessage.TeleportCause.SPAWN), location.getServer());
		}
	}

    @MessageHandler(ClientRequestPlayerListMessage.class)
    public void onClientRequestPlayerListMessage(ClientRequestPlayerListMessage message, String server) {
	   essentialsVC.getMessenger().sendMessage(new ServerUpdatePlayersMessage(essentialsVC.getProxy().getPlayers().parallelStream()
			 .map(CommandSender::getName).collect(Collectors.toSet())), server);
    }

    @MessageHandler(ClientTeleportToPlayerMessage.class)
    public void onClientTeleportToPlayerMessage(ClientTeleportToPlayerMessage message, String server) {
    	/**
    	if (message.getTarget().equalsIgnoreCase("test")) {
			ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
    		ProxiedPlayer target = essentialsVC.getProxy().getPlayer("Stanic");
			UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());

			essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(target.getUniqueId(), TeleportStateMessage.State.TELEPORTING),
					target.getServer().getInfo().getName());

			essentialsVC.getMessenger().sendMessage(new UpdateBackMessage(target.getUniqueId(), new LocationObject()), target.getServer().getInfo().getName());
			essentialsVC.getTeleportTask().addTeleport(essentialsVC.getUserRepository().get(target.getUniqueId()), userAccount, ess -> {
				ServerInfo targetInfo = ess.getTeleporting().getTarget().getPlayer().getServer().getInfo();
				if (ess.getAttempt() != null) {
					ess.setLastLocation(ess.getAttempt());
					ess.setAttempt(null);
				}
				if (!ess.getPlayer().getServer().getInfo().getName().equals(targetInfo.getName()))
					ess.getPlayer().connect(targetInfo);
				//else essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(ess.getPlayer().getUniqueId(), TeleportStateMessage.State.CANCEL),
							//ess.getPlayer().getServer().getInfo().getName());

				essentialsVC.getMessenger().sendMessage(new ServerTeleportToPlayerMessage(ess.getPlayer().getName(),
						ess.getTeleporting().getTarget().getPlayer().getName()), targetInfo.getName());
			});

			player.sendMessage(essentialsVC.getConfigValue().getMessage().TPA_ACCEPT.replacePlayer(target.getName()));
		}
		 **/
		ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
		if (player == null) return;
		if (message.getCause() == ClientTeleportToPlayerMessage.Cause.TP) {
			ProxiedPlayer target = essentialsVC.getProxy().getPlayer(message.getTarget());
			if (target == null || !essentialsVC.getServers().contains(target.getServer().getInfo().getName())) {
				player.sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
				return;
			}
			UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());
			if (userAccount != null)
				userAccount.setLastLocation(new ServerLocationObject(server, message.getLocation()));
			if (!player.getServer().getInfo().getName().equals(target.getServer().getInfo().getName()))
				player.connect(target.getServer().getInfo());

			essentialsVC.getMessenger().sendMessage(
					new ServerTeleportToPlayerMessage(player.getName(), target.getName()), target.getServer().getInfo().getName());

			player.sendMessage(essentialsVC.getConfigValue().getMessage().TP.replacePlayer(target.getName()));
		} else if (message.getCause() == ClientTeleportToPlayerMessage.Cause.TPA) {
			ProxiedPlayer target = essentialsVC.getProxy().getPlayer(message.getTarget());
			if (target == null || !essentialsVC.getServers().contains(target.getServer().getInfo().getName())) {
				player.sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
				return;
			}
			UserAccount userAccount = essentialsVC.getUserRepository().get(target.getUniqueId());
			if (userAccount == null || !essentialsVC.getServers().contains(userAccount.getPlayer().getServer().getInfo().getName())) {
				player.sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
				return;
			}
			userAccount.setTpaRequest(new Tuple<>(player.getUniqueId(),
					System.currentTimeMillis() + 120_000));

			player.sendMessage(essentialsVC.getConfigValue().getMessage().TPA_SENT.replacePlayer(target.getName()));
			target.sendMessage(essentialsVC.getConfigValue().getMessage().TPA_REQUEST.replacePlayer(player.getName()));
		} else {
			UserAccount userAccount = essentialsVC.getUserRepository().get(player.getUniqueId());
			Tuple<UUID, Long> tuple = userAccount.getTpaRequest();
			if (tuple == null || System.currentTimeMillis() > tuple.getB()) {
				userAccount.setTpaRequest(null);
				player.sendMessage(essentialsVC.getConfigValue().getMessage().NO_TPA_TO_ACCEPT.get());
				return;
			}
			ProxiedPlayer target = essentialsVC.getProxy().getPlayer(tuple.getA());
			if (target == null || !essentialsVC.getServers().contains(target.getServer().getInfo().getName())) {
				player.sendMessage(essentialsVC.getConfigValue().getMessage().PLAYER_NOT_FOUND.get());
				return;
			}

			if (!userAccount.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown"))
				essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(target.getUniqueId(), TeleportStateMessage.State.TELEPORTING),
						target.getServer().getInfo().getName());

			essentialsVC.getMessenger().sendMessage(new UpdateBackMessage(target.getUniqueId(), new LocationObject()), target.getServer().getInfo().getName());
			essentialsVC.getTeleportTask().addTeleport(essentialsVC.getUserRepository().get(target.getUniqueId()), userAccount, ess -> {
				ServerInfo targetInfo = ess.getTeleporting().getTarget().getPlayer().getServer().getInfo();
				if (ess.getAttempt() != null) {
					ess.setLastLocation(ess.getAttempt());
					ess.setAttempt(null);
				}
				if (!ess.getPlayer().getServer().getInfo().getName().equals(targetInfo.getName()))
					ess.getPlayer().connect(targetInfo);

				essentialsVC.getMessenger().sendMessage(new ServerTeleportToPlayerMessage(ess.getPlayer().getName(),
						ess.getTeleporting().getTarget().getPlayer().getName()), targetInfo.getName());
			});
			userAccount.setTpaRequest(null);
			player.sendMessage(essentialsVC.getConfigValue().getMessage().TPA_ACCEPT.replacePlayer(target.getName()));
			target.sendMessage(essentialsVC.getConfigValue().getMessage().TPA_ACCEPTED.replacePlayer(player.getName()));
		}
	}

    @MessageHandler(TeleportStateMessage.class)
    public void onTeleportStateMessage(TeleportStateMessage message, String server) {
	   UserAccount userAccount = essentialsVC.getUserRepository().get(message.getPlayer());
	   if (userAccount == null) return;
	   if (message.getState() == TeleportStateMessage.State.CANCEL) {
		  userAccount.setTeleporting(null);
		  userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().TPA_CANCELLED.get());
	   }
    }

    @MessageHandler(UpdateBackMessage.class)
    public void onUpdateBackMessage(UpdateBackMessage message, String server) {
	   UserAccount userAccount = essentialsVC.getUserRepository().get(message.getPlayer());
	   if (userAccount == null) return;
	   userAccount.setAttempt(new ServerLocationObject(server, message.getLocation()));
    }

    @MessageHandler(ClientUpdateBackMessage.class)
    public void onClientUpdateBackMessage(ClientUpdateBackMessage message, String server) {
	   UserAccount userAccount = essentialsVC.getUserRepository().get(message.getPlayer());
	   if (userAccount == null) return;
	   userAccount.setLastLocation(new ServerLocationObject(server, message.getLocation()));
    }

    @MessageHandler(ClientBackMessage.class)
    public void onClientBackMessage(ClientBackMessage message, String server) {
	   UserAccount userAccount = essentialsVC.getUserRepository().get(message.getPlayer());
	   if (userAccount == null) return;
	   if (userAccount.getLastLocation() == null) {
		  userAccount.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().CANT_BACK.get());
		  return;
	   }

	   if (!userAccount.getPlayer().hasPermission("essentialsvc.tp.bypasscooldown"))
		  essentialsVC.getMessenger().sendMessage(new TeleportStateMessage(userAccount.getPlayer().getUniqueId(), TeleportStateMessage.State.TELEPORTING),
				server);

	   essentialsVC.getTeleportTask().addTeleport(userAccount, userAccount.getLastLocation(), ess -> {
		  ServerLocationObject location = ess.getLastLocation();
		  ess.setLastLocation(new ServerLocationObject(server, message.getLocation()));
		  if (!ess.getPlayer().getServer().getInfo().getName().equals(location.getServer()))
			 ess.getPlayer().connect(essentialsVC.getProxy().getServerInfo(location.getServer()));

		  essentialsVC.getMessenger().sendMessage(
				new ServerTeleportToLocationMessage(ess.getPlayer().getName(), location, ServerTeleportToLocationMessage.TeleportCause.BACK), location.getServer());
		  ess.getPlayer().sendMessage(essentialsVC.getConfigValue().getMessage().BACK.get());
	   });
    }

    @MessageHandler(ClientHelpMessage.class)
    public void onClientRequestHelpMessage(ClientHelpMessage message, String server) {
	   ProxiedPlayer player = essentialsVC.getProxy().getPlayer(message.getPlayer());
	   if (player == null) return;
	   essentialsVC.getHelp().send(player, message.getType());
    }
}
