package me.jasonhorkles.filecleanervelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.jasonhorkles.filecleaner.CleanFiles;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

@Plugin(id = "filecleaner", name = "FileCleaner", version = "v%VERSION%", url = "https://github.com/SilverstoneMC/FileCleaner", description = "Clean your old files!", authors = {"JasonHorkles"})
public class FCVelocity {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public FCVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        loadConfig();
        cleanFiles();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("filecleanervelocity").aliases("fcv")
            .plugin(this).build();

        SimpleCommand simpleCommand = new Commands(this);

        commandManager.register(commandMeta, simpleCommand);
    }

    //todo make working config code
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
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                .load(new File(getDataFolder(), "config.yml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanFiles() {
        logger.info("Starting file cleaning task...");
        for (String folders : config.getSection("folders").getKeys()) {
            String folder = config.getString("folders." + folders + ".location");

            if (folder.equals("")) continue;

            int age = config.getInt("folders." + folders + ".age");
            int count = config.getInt("folders." + folders + ".count");
            long size = config.getLong("folders." + folders + ".size");
            new CleanFiles().CleanFilesTask(folder, logger, age, count, size);
        }
        logger.info("Done!");
    }
}