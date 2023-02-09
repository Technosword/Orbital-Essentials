package it.thedarksword.essentialsvc.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class Config {

    private File file;
    private FileConfiguration fileConfiguration;

    private Plugin plugin;
    private String fileName;

    public Config(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName + ".yml";
    }

    public FileConfiguration get() {
        return fileConfiguration;
    }

    public void load() throws IOException, InvalidConfigurationException {
        File pluginFolder = new File(plugin.getDataFolder().getPath());
        if (!pluginFolder.exists()) pluginFolder.mkdirs();

        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            InputStream inputStream = plugin.getResource(fileName);
            Files.copy(Objects.requireNonNull(inputStream), file.toPath());
        }

        fileConfiguration = new YamlConfiguration();
        fileConfiguration.load(file);
    }
    public void reload() {
        try {
            load();
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
