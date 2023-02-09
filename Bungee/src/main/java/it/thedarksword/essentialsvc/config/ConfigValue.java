package it.thedarksword.essentialsvc.config;

import com.google.common.collect.Maps;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import it.thedarksword.essentialsvc.yaml.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class ConfigValue {

    private final Homes homes;
    private final Message message;

    public ConfigValue(Configuration configuration) {
        homes = new Homes(configuration);
        message = new Message(configuration);
    }

    public static final class Homes {
        private final Map<String, Integer> homes = new HashMap<>();

        private Homes(Configuration configuration) {
            String path = "homes.";
            for(String perm : configuration.getSection("homes").getKeys()) {
                homes.put(perm, configuration.getInt(path + perm));
            }
        }

        public int getMaxHomes(String perm) {
            return homes.get(perm);
        }

        public Set<Map.Entry<String, Integer>> entrySet() {
            return homes.entrySet();
        }
    }

    public static final class Message {
        public final ConfigComponent INSUFFICIENT_PERMISSION;
        public final ConfigComponent NOT_PLAYER;
        public final ConfigComponent INVALID_ARGUMENTS;
        public final ConfigComponent PLAYER_NOT_FOUND;
        public final ConfigComponent TP;
        public final ConfigComponent TPA_SENT;
        public final ConfigComponent TPA_REQUEST;
        public final ConfigComponent NO_TPA_TO_ACCEPT;
        public final ConfigComponent TPA_ACCEPT;
        public final ConfigComponent TPA_ACCEPTED;
        public final ConfigComponent TP_DENY;
        public final ConfigComponent TPA_CANCELLED;
        public final ConfigComponent TELEPORTING;
        public final ConfigComponent SPAWN_SET;
        public final ConfigComponent SPAWN_NOT_SET;
        public final ConfigComponent SPAWN;
        public final ConfigComponent CANT_BACK;
        public final ConfigComponent BACK;
        public final ConfigComponent MAX_HOMES;
        public final ConfigComponent NO_HOME_NAME;
        public final ConfigComponent HOME_SET;
        public final ConfigComponent HOME_REMOVED;
        public final ConfigComponent HOME_LIST;
        public final ConfigComponent HOME;
        public final ConfigComponent GAMEMODE;
        public final ConfigComponent GOD;
        public final ConfigComponent FLY;
        public final ConfigComponent CANT_INVSEE;
        public final ConfigComponent REPAIR_FAILED;
        public final ConfigComponent REPAIR;
        public final ConfigComponent HELPOP;
        public final ConfigComponent MESSAGE_FROM;
        public final ConfigComponent MESSAGE_TO;
        public final ConfigComponent NICKNAME;

        private final Map<ConfigMessage, ConfigComponent> values = Maps.newEnumMap(ConfigMessage.class);

        private Message(Configuration configuration) {
            String path = "messages.";
            INSUFFICIENT_PERMISSION = getTranslated(configuration.getString(path + "insufficient-permission"));
            NOT_PLAYER = getTranslated(configuration.getString(path + "not-player"));
            INVALID_ARGUMENTS = getTranslated(configuration.getString(path + "invalid-arguments"));
            PLAYER_NOT_FOUND = getTranslated(configuration.getString(path + "player-not-found"));
            TP = getTranslated(configuration.getString(path + "tp"));
            TPA_SENT = getTranslated(configuration.getString(path + "tpa-sent"));
            TPA_REQUEST = getTranslated(configuration.getString(path + "tpa-request"));
            NO_TPA_TO_ACCEPT = getTranslated(configuration.getString(path + "no-tpa-to-accept"));
            TPA_ACCEPT = getTranslated(configuration.getString(path + "tpa-accept"));
            TPA_ACCEPTED = getTranslated(configuration.getString(path + "tpa-accepted"));
            TP_DENY = getTranslated(configuration.getString(path + "tp-deny"));
            TPA_CANCELLED = getTranslated(configuration.getString(path + "tpa-cancelled"));
            TELEPORTING = getTranslated(configuration.getString(path + "teleporting"));
            SPAWN_SET = getTranslated(configuration.getString(path + "spawn-set"));
            SPAWN_NOT_SET = getTranslated(configuration.getString(path + "spawn-not-set"));
            SPAWN = getTranslated(configuration.getString(path + "spawn"));
            CANT_BACK = getTranslated(configuration.getString(path + "cant-back"));
            BACK = getTranslated(configuration.getString(path + "back"));
            MAX_HOMES = getTranslated(configuration.getString(path + "max-homes"));
            NO_HOME_NAME = getTranslated(configuration.getString(path + "no-home-name"));
            HOME_SET = getTranslated(configuration.getString(path + "home-set"));
            HOME_REMOVED = getTranslated(configuration.getString(path + "home-removed"));
            HOME_LIST = getTranslated(configuration.getString(path + "home-list"));
            HOME = getTranslated(configuration.getString(path + "home"));
            GAMEMODE = getTranslated(configuration.getString(path + "gamemode"));
            GOD = getTranslated(configuration.getString(path + "god"));
            FLY = getTranslated(configuration.getString(path + "fly"));
            CANT_INVSEE = getTranslated(configuration.getString(path + "cant-invsee"));
            REPAIR_FAILED = getTranslated(configuration.getString(path + "repair-failed"));
            REPAIR = getTranslated(configuration.getString(path + "repair"));
            HELPOP = getTranslated(configuration.getString(path + "helpop"));
            MESSAGE_FROM = getTranslated(configuration.getString(path + "message-from"));
            MESSAGE_TO = getTranslated(configuration.getString(path + "message-to"));
            NICKNAME = getTranslated(configuration.getString(path + "nickname"));

            values.put(ConfigMessage.INSUFFICIENT_PERMISSION, INSUFFICIENT_PERMISSION);
            values.put(ConfigMessage.INVALID_ARGUMENTS, INVALID_ARGUMENTS);
            values.put(ConfigMessage.PLAYER_NOT_FOUND, PLAYER_NOT_FOUND);
            values.put(ConfigMessage.TP, TP);
            values.put(ConfigMessage.TPA_SENT, TPA_SENT);
            values.put(ConfigMessage.TPA_REQUEST, TPA_REQUEST);
            values.put(ConfigMessage.NO_TPA_TO_ACCEPT, NO_TPA_TO_ACCEPT);
            values.put(ConfigMessage.TPA_ACCEPT, TPA_ACCEPT);
            values.put(ConfigMessage.TPA_ACCEPTED, TPA_ACCEPTED);
            values.put(ConfigMessage.TP_DENY, TP_DENY);
            values.put(ConfigMessage.TPA_CANCELLED, TPA_CANCELLED);
            values.put(ConfigMessage.TELEPORTING, TELEPORTING);
            values.put(ConfigMessage.SPAWN_SET, SPAWN_SET);
            values.put(ConfigMessage.SPAWN_NOT_SET, SPAWN_NOT_SET);
            values.put(ConfigMessage.SPAWN, SPAWN);
            values.put(ConfigMessage.CANT_BACK, CANT_BACK);
            values.put(ConfigMessage.BACK, BACK);
            values.put(ConfigMessage.MAX_HOMES, MAX_HOMES);
            values.put(ConfigMessage.NO_HOME_NAME, NO_HOME_NAME);
            values.put(ConfigMessage.HOME_SET, HOME_SET);
            values.put(ConfigMessage.HOME_REMOVED, HOME_REMOVED);
            values.put(ConfigMessage.HOME_LIST, HOME_LIST);
            values.put(ConfigMessage.HOME, HOME);
            values.put(ConfigMessage.GAMEMODE, GAMEMODE);
            values.put(ConfigMessage.GOD, GOD);
            values.put(ConfigMessage.FLY, FLY);
            values.put(ConfigMessage.CANT_INVSEE, CANT_INVSEE);
            values.put(ConfigMessage.REPAIR_FAILED, REPAIR_FAILED);
            values.put(ConfigMessage.REPAIR, REPAIR);
            values.put(ConfigMessage.HELPOP, HELPOP);
            values.put(ConfigMessage.MESSAGE_FROM, MESSAGE_FROM);
            values.put(ConfigMessage.MESSAGE_TO, MESSAGE_TO);
            values.put(ConfigMessage.NICKNAME, NICKNAME);
        }

        public ConfigComponent byName(ConfigMessage configMessage) {
            return values.get(configMessage);
        }

        private ConfigComponent getTranslated(String message) {
            return new ConfigComponent(ChatColor.translateAlternateColorCodes('&', message));
        }

        @RequiredArgsConstructor
        public static final class ConfigComponent {
            private final String message;

            public TextComponent get() {
                return new TextComponent(message);
            }

            public TextComponent getReplaced(String from, String to) {
                return new TextComponent(message.replace(from, to));
            }

            public TextComponent getReplaced(String[] froms, String[] to) {
                if(froms.length != to.length) throw new IllegalArgumentException("Array sizes are not equals");
                String message = this.message;
                for(int i = 0; i < froms.length; i++) {
                    message = message.replace(froms[i], to[i]);
                }
                return new TextComponent(message);
            }

            public TextComponent replacePlayer(String to) {
                return getReplaced("{player}", to);
            }
        }
    }
}
