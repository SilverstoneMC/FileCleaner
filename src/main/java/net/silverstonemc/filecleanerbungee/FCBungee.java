package net.silverstonemc.filecleanerbungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.silverstonemc.filecleaner.CleanFiles;
import net.silverstonemc.filecleaner.VersionChecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class FCBungee extends Plugin implements Listener {

    private Configuration config;

    @Override
    public void onEnable() {
        loadConfig();

        getProxy().getPluginManager().registerCommand(this, new Commands(this));

        getProxy().getPluginManager().registerListener(this, new BungeeUpdateChecker(this));

        // Log version update
        FCBungee instance = this;
        getProxy().getScheduler().schedule(
            this, () -> {
                String latest = new VersionChecker().getLatestVersion();
                String current = instance.getDescription().getVersion().replace("b", "");

                if (latest == null) return;
                if (!current.equals(latest)) new BungeeUpdateChecker(instance).logUpdate(current, latest);
            }, 500L, TimeUnit.MILLISECONDS);

        cleanFiles();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadConfig() {
        // Generate config if it doesn't exist
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) try (InputStream in = getResourceAsStream("config.yml")) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Load the config
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(
                getDataFolder(),
                "config.yml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanFiles() {
        getLogger().info("Starting file cleaning task...");
        CleanFiles cleanFiles = new CleanFiles();

        // Clean directories
        for (String key : config.getSection("folders").getKeys()) {
            String folder = config.getString("folders." + key + ".location");

            if (folder.isEmpty()) continue;

            int age = config.getInt("folders." + key + ".age");
            int count = config.getInt("folders." + key + ".count");
            long size = config.getLong("folders." + key + ".size");

            cleanFiles.cleanFilesInDir(folder, getLogger(), age, count, size);
        }

        // Clean individual files
        for (String key : config.getSection("files").getKeys()) {
            String file = config.getString("files." + key + ".location");

            if (file.isEmpty()) continue;

            int age = config.getInt("files." + key + ".age");
            long size = config.getLong("files." + key + ".size");

            cleanFiles.cleanFiles(file, getLogger(), age, size);
        }

        getLogger().info("Done!");
    }
}
