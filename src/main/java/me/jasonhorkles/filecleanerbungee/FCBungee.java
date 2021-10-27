package me.jasonhorkles.filecleanerbungee;

import me.jasonhorkles.filecleaner.CleanFiles;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@SuppressWarnings("unused")
public class FCBungee extends Plugin implements Listener {

    private FCBungee instance;
    private Configuration config;

    @Override
    // Startup
    public void onEnable() {
        instance = this;

        loadConfig();

        getLogger().info("Plugin location: \"" + instance.getFile().getAbsolutePath() + "\"");

        getProxy().getPluginManager().registerCommand(this, new Commands(instance));

        cleanFiles();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadConfig() {
        // Generate config if it doesn't exist
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) try (InputStream in = getResourceAsStream("config.yml")) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Load the config
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanFiles() {
        instance.getLogger().info("Starting file cleaning task...");
        for (String folders : config.getSection("folders").getKeys()) {
            String folder = config.getString("folders." + folders + ".location");

            if (folder.equals("")) continue;

            int age = config.getInt("folders." + folders + ".age");
            int count = config.getInt("folders." + folders + ".count");
            long size = config.getLong("folders." + folders + ".size");
            new CleanFiles().CleanFilesTask(folder, null, instance, age, count, size);
        }
        instance.getLogger().info("Done!");
    }
}
