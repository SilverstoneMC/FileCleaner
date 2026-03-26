/*
 * FileCleaner - a Minecraft plugin
 * Copyright (C) 2026 JasonHorkles and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package net.silverstonemc.filecleanervelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.filecleaner.CleanFiles;
import net.silverstonemc.filecleaner.VersionChecker;

import org.bstats.velocity.Metrics;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Plugin(id = "filecleaner", name = "FileCleaner", version = "v%VERSION%", url = "https://github.com/SilverstoneMC/FileCleaner", description = "Clean your old files!", authors = {"JasonHorkles"})
public class FCVelocity {
    @Inject
    public FCVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;

        loadConfig();

        cleanFiles(server.getConsoleCommandSource());
    }

    public final ProxyServer server;
    public final Logger logger;
    private final Metrics.Factory metricsFactory;

    private ConfigurationNode config;
    private final Path dataDirectory;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        metricsFactory.make(this, 30402);

        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("filecleanervelocity").aliases("fcv")
            .plugin(this).build();

        SimpleCommand simpleCommand = new Commands(this);
        commandManager.register(commandMeta, simpleCommand);

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new VelocityUpdateChecker(this));

        // Log version update
        FCVelocity instance = this;
        server.getScheduler().buildTask(
            this, () -> {
                String latest = new VersionChecker().getLatestVersion();
                //noinspection OptionalGetWithoutIsPresent because it should exist
                String current = server.getPluginManager()
                    .getPlugin(VelocityUpdateChecker.pluginName.toLowerCase()).get().getDescription()
                    .getVersion().get().replace("v", "");

                if (latest == null) return;
                if (!current.equals(latest)) new VelocityUpdateChecker(instance).logUpdate(current, latest);
            }).delay(500L, TimeUnit.MILLISECONDS).schedule();
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
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(file.toPath()).build();

        try {
            config = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanFiles(CommandSource sender) {
        sender.sendMessage(Component.text(
            "[FileCleaner] Starting file cleaning task...",
            NamedTextColor.GOLD));
        CleanFiles cleanFiles = new CleanFiles();

        // Clean directories
        for (ConfigurationNode node : config.node("folders").childrenMap().values()) {
            if (node.key() == null) continue;

            String folder = config.node("folders", node.key(), "location").getString();

            if (folder == null) continue;

            int age = config.node("folders", node.key(), "age").getInt();
            int count = config.node("folders", node.key(), "count").getInt();
            long size = config.node("folders", node.key(), "size").getLong();
            List<String> excludedFiles;
            try {
                excludedFiles = config.node("folders", node.key(), "exclude").getList(String.class);
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

            cleanFiles.scanFilesInDir(folder, logger, age, count, size, excludedFiles);
        }

        // Clean individual files
        for (ConfigurationNode node : config.node("files").childrenMap().values()) {
            if (node.key() == null) continue;

            String file = config.node("files", node.key(), "location").getString();

            if (file == null) continue;

            int age = config.node("files", node.key(), "age").getInt();
            long size = config.node("files", node.key(), "size").getLong();

            cleanFiles.scanFile(file, logger, age, size);
        }

        String s = CleanFiles.filesDeleted == 1 ? "" : "s";
        sender.sendMessage(Component.text(
            "[FileCleaner] Done! " + CleanFiles.filesDeleted + " file" + s + " deleted, saving " + CleanFiles.mbSaved + " MB",
            NamedTextColor.DARK_GREEN));

        CleanFiles.filesDeleted = 0;
        CleanFiles.mbSaved = 0;
    }
}