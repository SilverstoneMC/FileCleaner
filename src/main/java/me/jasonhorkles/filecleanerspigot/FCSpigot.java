package me.jasonhorkles.filecleanerspigot;

import me.jasonhorkles.filecleaner.CleanFiles;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class FCSpigot extends JavaPlugin {

    private static FCSpigot instance;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        instance = this;

        getCommand("filecleaner").setTabCompleter(new TabComplete());

        saveDefaultConfig();

        getLogger().info("Plugin location: \"" + instance.getFile().getAbsolutePath() + "\"");

        cleanFiles();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args[0].toLowerCase()) {
            case "reload":
                saveDefaultConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "FileCleaner reloaded!");
                return true;
            case "cleannow":
                if (sender instanceof Player)
                    sender.sendMessage(ChatColor.DARK_GREEN + "Cleaning files! Check console for more information.");
                cleanFiles();
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void cleanFiles() {
        instance.getLogger().info("Starting file cleaning task...");
        for (String folders : instance.getConfig().getConfigurationSection("folders").getKeys(false)) {
            String file = instance.getConfig().getString("folders." + folders + ".location");

            if (file == null) continue;

            int age = instance.getConfig().getInt("folders." + folders + ".age");
            int count = instance.getConfig().getInt("folders." + folders + ".count");
            new CleanFiles().CleanFilesTask(file, instance, null, age, count);
        }
        instance.getLogger().info("Done!");
    }
}