package it.thedarksword.essentialsvc.chat;

import it.thedarksword.essentialsvc.concurrent.WeakConcurrentHashMap;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ClanTagChat {

	public WeakConcurrentHashMap<UUID, Map.Entry<String, String>> cache = new WeakConcurrentHashMap<>(TimeUnit.SECONDS.toMillis(10));
	private final SimpleClans clans = SimpleClans.getInstance();

	public @NotNull String getClanTag(Player player) {
		ClanPlayer clanPlayer = clans.getClanManager().clanPlayers.get(player.getUniqueId().toString());

		if (clanPlayer == null)
			return "";
		if (clanPlayer.getClan() == null || !clanPlayer.isTagEnabled()) return "";
		if (cache.containsKey(player.getUniqueId())) return cache.get(player.getUniqueId()).getValue();

		Map.Entry<String, String> tag = new AbstractMap.SimpleEntry<>(clanPlayer.getClan().getName(), clanPlayer.getTagLabel());
		cache.put(player.getUniqueId(), tag);
		return tag.getValue();
	}

	public @NotNull ClanPlayer.Channel getPlayerChannel(Player player) {
		ClanPlayer clanPlayer = clans.getClanManager().clanPlayers.get(player.getUniqueId().toString());

		if (clanPlayer == null)
			return ClanPlayer.Channel.NONE;

		return clanPlayer.getChannel();
	}

}