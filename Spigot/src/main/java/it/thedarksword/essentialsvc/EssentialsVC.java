package it.thedarksword.essentialsvc;

import it.thedarksword.essentialsvc.chat.Chat;
import it.thedarksword.essentialsvc.chat.ChatConfig;
import it.thedarksword.essentialsvc.commands.*;
import it.thedarksword.essentialsvc.config.Config;
import it.thedarksword.essentialsvc.listeners.PlayerListener;
import it.thedarksword.essentialsvc.listeners.WorldListener;
import it.thedarksword.essentialsvc.messaging.AbstractMessenger;
import it.thedarksword.essentialsvc.messaging.Messenger;
import it.thedarksword.essentialsvc.player.EssPlayer;
import it.thedarksword.essentialsvc.tasks.TeleportTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class EssentialsVC extends JavaPlugin {

    private Messenger messenger;

    private final HashMap<String, Consumer<Player>> messageOnLogin = new HashMap<>();
    private final HashMap<String, EssPlayer> essPlayer = new HashMap<>();

    private TeleportTask teleportTask;

    private final Set<String> networkPlayers = new HashSet<>();

    @Setter private ChatConfig chatConfig;

    private final Config chats = new Config(this, "chats");

    @Override
    public void onEnable() {
        messenger = new Messenger(this);
        try {
            chats.load();
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }

        getServer().getMessenger().registerIncomingPluginChannel(this, AbstractMessenger.CHANNEL, messenger);
        getServer().getMessenger().registerOutgoingPluginChannel(this, AbstractMessenger.CHANNEL);

        teleportTask = new TeleportTask(this);
        getServer().getScheduler().runTaskTimer(this, teleportTask, 20, 20);

        for(World world : getServer().getWorlds()) {
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        }

        HashMap<String, String> groups = new HashMap<>();
        if (chats.get().getConfigurationSection("group-formats") != null) {
            for (String key : chats.get().getConfigurationSection("group-formats").getKeys(false)) {
                groups.put(key, chats.get().getString("group-formats." + key));
            }
        }
        setChatConfig(new ChatConfig(chats.get().getString("chat-format"), chats.get().getInt("chat-distance"), groups));

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    private void registerCommands() {
        getCommand("essentialsvc").setExecutor(new EssentialsVCCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("enderchest").setExecutor(new EnderchestCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("gamemode").setExecutor(new GameModeCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("helpop").setExecutor(new HelpopCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("homelist").setExecutor(new HomeListCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this));
        getCommand("repair").setExecutor(new RepairCommand(this));
        getCommand("nickname").setExecutor(new NicknameCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tp").setExecutor(new TPCommand(this));
        getCommand("tpdeny").setExecutor(new TPDenyCommand(this));
        getCommand("workbench").setExecutor(new WorkbenchCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new Chat(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }

    public String chatBuilder(String[] args) {
        return chatBuilder(args, 0);
    }

    public String chatBuilder(String[] args, int start) {
        if(args.length <= start) throw new IllegalArgumentException("Invalid args length");
        StringBuilder builder = new StringBuilder(args[start]);
        for(int i = start+1; i < args.length; i++) {
            builder.append(" ").append(args[i]);
        }
        return builder.toString();
    }
}
