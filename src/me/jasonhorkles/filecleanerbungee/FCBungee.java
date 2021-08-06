package me.jasonhorkles.filecleanerbungee;

import me.jasonhorkles.filecleaner.CleanFiles;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FCBungee extends Plugin implements Listener {

    private static FCBungee instance;
    private static Configuration config;

    @Override
    // Startup
    public void onEnable() {
        instance = this;

        loadConfig();

        getLogger().info("Plugin location: \"" + instance.getFile().getAbsolutePath() + "\"");

        getProxy().getPluginManager().registerCommand(this, new Commands());

        cleanFiles();
    }

    public class Commands extends Command implements TabExecutor {

        public Commands() {
            super("filecleanerbungee", "filecleaner.command", "fcb");
        }

        public void execute(CommandSender sender, String[] args) {
            if (args.length == 0) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /filecleanerbungee <reload | cleannow>"));
                return;
            }

            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("filecleaner.reload")) {
                loadConfig();
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "FileCleaner reloaded!"));

            } else if (args[0].equalsIgnoreCase("cleannow") && sender.hasPermission("filecleaner.cleannow")) {
                if (sender instanceof ProxiedPlayer)
                    sender.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "Cleaning files! Check console for more information."));
                cleanFiles();

            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /filecleanerbungee <reload | cleannow>"));
            }
        }


        final List<String> arguments = new ArrayList<>();

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            if (sender.hasPermission("filecleaner.reload")) {
                if (!arguments.contains("reload")) {
                    arguments.add("reload");
                }
            } else {
                arguments.remove("reload");
            }

            if (sender.hasPermission("filecleaner.cleannow")) {
                if (!arguments.contains("cleannow")) {
                    arguments.add("cleannow");
                }
            } else {
                arguments.remove("cleannow");
            }

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadConfig() {
        // Generate config if it doesn't exist
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // Load the config
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cleanFiles() {
        instance.getLogger().info("Starting file cleaning task...");
        for (String folders : config.getSection("folders").getKeys()) {
            String file = config.getString("folders." + folders + ".location");

            if (file.equals("")) continue;

            int days = config.getInt("folders." + folders + ".age");
            CleanFiles.CleanFilesTask(file, null, instance, days);
        }
        instance.getLogger().info("Done!");
    }
}
