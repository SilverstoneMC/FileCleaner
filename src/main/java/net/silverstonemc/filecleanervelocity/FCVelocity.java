package net.silverstonemc.filecleanervelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.silverstonemc.filecleaner.CleanFiles;
import net.silverstonemc.filecleaner.VersionChecker;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Plugin(id = "filecleaner", name = "FileCleaner", version = "v%VERSION%", url = "https://github.com/SilverstoneMC/FileCleaner", description = "Clean your old files!", authors = {"JasonHorkles"})
public class FCVelocity {
    @Inject
    public FCVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        loadConfig();

        // Log version update
        FCVelocity instance = this;
        server.getScheduler().buildTask(this, () -> {
            String latest = new VersionChecker().getLatestVersion();
            //noinspection OptionalGetWithoutIsPresent because it should exist
            String current = server.getPluginManager()
                .getPlugin(VelocityUpdateChecker.pluginName.toLowerCase()).get().getDescription().getVersion()
                .get().replace("v", "");

            if (latest == null) return;
            if (!current.equals(latest)) new VelocityUpdateChecker(instance).logUpdate(current, latest);
        }).delay(100L, TimeUnit.MILLISECONDS).schedule();

        cleanFiles();
    }

    public final ProxyServer server;
    public final Logger logger;

    private ConfigurationNode config;
    private final Path dataDirectory;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("filecleanervelocity").aliases("fcv")
            .plugin(this).build();

        SimpleCommand simpleCommand = new Commands(this);
        commandManager.register(commandMeta, simpleCommand);

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new VelocityUpdateChecker(this));
    }

    public void loadConfig() {
        // Generate config if it doesn't exist
        if (!dataDirectory.toFile().exists()) //noinspection ResultOfMethodCallIgnored
            dataDirectory.toFile().mkdir();

        File file = new File(dataDirectory.toFile(), "config.yml");

        if (!file.exists())
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(Objects.requireNonNull(in), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        // Load the config
        YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(file.toPath()).build();

        try {
            config = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanFiles() {
        logger.info("Starting file cleaning task...");
        for (ConfigurationNode folders : config.getNode("folders").getChildrenMap().values()) {
            if (folders.getKey() == null) continue;

            String folder = config.getNode("folders", folders.getKey(), "location").getString();

            if (folder == null) continue;

            int age = config.getNode("folders", folders.getKey(), "age").getInt();
            int count = config.getNode("folders", folders.getKey(), "count").getInt();
            long size = config.getNode("folders", folders.getKey(), "size").getLong();
            new CleanFiles().CleanFilesTask(folder, logger, age, count, size);
        }
        logger.info("Done!");
    }
}