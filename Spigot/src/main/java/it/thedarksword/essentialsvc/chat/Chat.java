package it.thedarksword.essentialsvc.chat;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.concurrent.WeakConcurrentHashMap;
import it.thedarksword.essentialsvc.listeners.ClanListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat implements Listener {

    private final EssentialsVC essentialsVC;
    private final LuckPerms luckPerms;

    public ClanTagChat clanTagChat = null;

    public Chat(EssentialsVC essentialsVC) {
	   this.essentialsVC = essentialsVC;
	   RegisteredServiceProvider<LuckPerms> provider = essentialsVC.getServer().getServicesManager().getRegistration(LuckPerms.class);
	   if (provider == null) throw new IllegalStateException("LuckPerms is not loaded");
	   luckPerms = provider.getProvider();

	   if (essentialsVC.getServer().getPluginManager().getPlugin("SimpleClans") != null) {
		   clanTagChat = new ClanTagChat();
		   essentialsVC.getServer().getPluginManager().registerEvents(new ClanListener(this), essentialsVC);
	   }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent event) {
		if (essentialsVC.getChatConfig() == null) return;
		if (clanTagChat.getPlayerChannel(event.getPlayer()) != ClanPlayer.Channel.NONE) return;

		event.setCancelled(true);

		final Player player = event.getPlayer();
		final String group = loadUser(player).getPrimaryGroup();
		final String message = event.getMessage();
		String clanTag = "";
		if (clanTagChat != null) {
			clanTag = clanTagChat.getClanTag(player);
		}

		String groupChat = essentialsVC.getChatConfig().getByGroup(group);
		String format = Objects.requireNonNull(groupChat != null ? groupChat : essentialsVC.getChatConfig().getGlobal())
				.replace("{world}", player.getWorld().getName())
				.replace("{prefix}", getPrefix(player))
				.replace("{prefixes}", getPrefixes(player))
				.replace("{name}", player.getName())
				.replace("{nickname}", player.getDisplayName())
				.replace("{suffix}", getSuffix(player))
				.replace("{suffixes}", getSuffixes(player))
				.replace("{username-color}", playerMeta(player).getMetaValue("username-color") != null
						? playerMeta(player).getMetaValue("username-color") : groupMeta(group).getMetaValue("username-color") != null
						? groupMeta(group).getMetaValue("username-color") : "")
				.replace("{message-color}", playerMeta(player).getMetaValue("message-color") != null
						? playerMeta(player).getMetaValue("message-color") : groupMeta(group).getMetaValue("message-color") != null
						? groupMeta(group).getMetaValue("message-color") : "");

		format = translateHexColorCodes(colorize(format));

		String finalFormat = format.replace("{clan_tag}", clanTag);
		Bukkit.getScheduler().callSyncMethod(essentialsVC, () -> {
			boolean received = false;
			for (Entity entity : player.getNearbyEntities(essentialsVC.getChatConfig().getChatDistance(), essentialsVC.getChatConfig().getChatDistance(), essentialsVC.getChatConfig().getChatDistance())) {
				if (entity instanceof Player) {
					if (entity.equals(player)) received = true;
					entity.sendMessage(finalFormat.replace("{message}", player.hasPermission("essentialsvc.colorcodes") && player.hasPermission("essentialsvc.rgbcodes")
							? translateHexColorCodes(colorize(message)) :
							player.hasPermission("essentialsvc.colorcodes") ? colorize(message) : player.hasPermission("essentialsvc.rgbcodes")
									? translateHexColorCodes(message) : message).replace("%", "%%"));
				}
			}

			if (!received)
				player.sendMessage(finalFormat.replace("{message}", player.hasPermission("essentialsvc.colorcodes") && player.hasPermission("essentialsvc.rgbcodes")
						? translateHexColorCodes(colorize(message)) :
						player.hasPermission("essentialsvc.colorcodes") ? colorize(message) : player.hasPermission("essentialsvc.rgbcodes")
								? translateHexColorCodes(message) : message).replace("%", "%%"));
			return true;
		}).isDone();
	}

    private String colorize(final String message) {
	   return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String translateHexColorCodes(final String message) {
	   final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
	   final char colorChar = ChatColor.COLOR_CHAR;

	   final Matcher matcher = hexPattern.matcher(message);
	   final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

	   while (matcher.find()) {
		  final String group = matcher.group(1);

		  matcher.appendReplacement(buffer, colorChar + "x"
				+ colorChar + group.charAt(0) + colorChar + group.charAt(1)
				+ colorChar + group.charAt(2) + colorChar + group.charAt(3)
				+ colorChar + group.charAt(4) + colorChar + group.charAt(5));
	   }

	   return matcher.appendTail(buffer).toString();
    }

    private String getPrefix(final Player player) {
	   final String prefix = playerMeta(player).getPrefix();

	   return prefix != null ? prefix : "";
    }

    private String getSuffix(final Player player) {
	   final String suffix = playerMeta(player).getSuffix();

	   return suffix != null ? suffix : "";
    }

    private String getPrefixes(final Player player) {
	   final SortedMap<Integer, String> map = playerMeta(player).getPrefixes();
	   final StringBuilder prefixes = new StringBuilder();

	   for (final String prefix : map.values())
		  prefixes.append(prefix);

	   return prefixes.toString();
    }

    private String getSuffixes(final Player player) {
	   final SortedMap<Integer, String> map = playerMeta(player).getSuffixes();
	   final StringBuilder suffixes = new StringBuilder();

	   for (final String prefix : map.values())
		  suffixes.append(prefix);

	   return suffixes.toString();
    }

    private CachedMetaData playerMeta(final Player player) {
	   return loadUser(player).getCachedData().getMetaData(luckPerms.getContextManager().getQueryOptions(player));
    }

    private CachedMetaData groupMeta(final String group) {
	   return loadGroup(group).getCachedData().getMetaData(luckPerms.getContextManager().getStaticQueryOptions());
    }

    private User loadUser(final Player player) {
	   if (!player.isOnline())
		  throw new IllegalStateException("Player is offline!");

	   return luckPerms.getUserManager().getUser(player.getUniqueId());
    }

    private Group loadGroup(final String group) {
	   return luckPerms.getGroupManager().getGroup(group);
    }

}
