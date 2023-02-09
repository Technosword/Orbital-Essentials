package it.thedarksword.essentialsvc.help;

import com.google.common.collect.Maps;

import it.thedarksword.essentialsvc.objets.HelpType;
import it.thedarksword.essentialsvc.yaml.Configuration;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class Help {

    private static final TextComponent TITLE =
            new TextComponent(ChatColor.GRAY + "[" + ChatColor.YELLOW + "EssentialsVC" + ChatColor.GRAY + "] " + ChatColor.YELLOW + " Commands Help");
    //private static final String CMD = ChatColor.GRAY + " - " + ChatColor.GOLD + "/{command} {arguments}" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "{description}";

    private final Map<HelpType, HelpMessage> help = Maps.newEnumMap(HelpType.class);

    public Help(Configuration configuration) {
        String path = "commands-description.";
        help.put(HelpType.TP, getCmd(configuration.getString(path + "tp")));
        help.put(HelpType.TPA, getCmd(configuration.getString(path + "tpa")));
        help.put(HelpType.TPACCEPT, getCmd(configuration.getString(path + "tpaccept")));
        help.put(HelpType.TPDENY, getCmd(configuration.getString(path + "tpdeny")));
        help.put(HelpType.BACK, getCmd(configuration.getString(path + "back")));
        help.put(HelpType.SETHOME, getCmd(configuration.getString(path + "sethome")));
        help.put(HelpType.DELHOME, getCmd(configuration.getString(path + "delhome")));
        help.put(HelpType.HOME, getCmd(configuration.getString(path + "home")));
        help.put(HelpType.HOMELIST, getCmd(configuration.getString(path + "homelist")));
        help.put(HelpType.SETSPAWN, getCmd(configuration.getString(path + "setspawn")));
        help.put(HelpType.SPAWN, getCmd(configuration.getString(path + "spawn")));
        help.put(HelpType.HELPOP, getCmd(configuration.getString(path + "helpop")));
        help.put(HelpType.MESSAGE, getCmd(configuration.getString(path + "message")));
        help.put(HelpType.REPLY, getCmd(configuration.getString(path + "reply")));
        help.put(HelpType.NICKNAME, getCmd(configuration.getString(path + "nickname")));
        help.put(HelpType.REPAIR, getCmd(configuration.getString(path + "repair")));
        help.put(HelpType.FLY, getCmd(configuration.getString(path + "fly")));
        help.put(HelpType.GOD, getCmd(configuration.getString(path + "god")));
        help.put(HelpType.ENDERCHEST, getCmd(configuration.getString(path + "enderchest")));
        help.put(HelpType.WORKBENCH, getCmd(configuration.getString(path + "workbench")));
        help.put(HelpType.INVSEE, getCmd(configuration.getString(path + "invsee")));
        help.put(HelpType.GAMEMODE, getCmd(configuration.getString(path + "gamemode")));
    }

    private HelpMessage getCmd(String command) {
        //return new HelpMessage(CMD.replace("{command}", command).replace("{arguments}", arguments).replace("{description}", description).trim());
        return new HelpMessage(ChatColor.translateAlternateColorCodes('&', command));
    }

    public void send(ProxiedPlayer player, HelpType type) {
        if(type == HelpType.ALL) {
            player.sendMessage(TITLE);
            for(HelpMessage helpMessage : help.values())
                player.sendMessage(helpMessage.getDescription());
        } else {
            player.sendMessage(help.get(type).getDescription());
        }
    }

    @Getter
    public static final class HelpMessage {
        private final TextComponent description;

        private HelpMessage(String description) {
            this.description = new TextComponent(description);
        }
    }
}
