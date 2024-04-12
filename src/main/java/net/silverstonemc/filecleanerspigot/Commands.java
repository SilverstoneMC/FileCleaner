package net.silverstonemc.filecleanerspigot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FCSpigot instance;

    public Commands(JavaPlugin plugin, FCSpigot instance) {
        this.plugin = plugin;
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false;

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.saveDefaultConfig();
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "FileCleaner reloaded!");
                return true;
            }
            case "cleannow" -> {
                if (sender instanceof Player) sender.sendMessage(
                    ChatColor.DARK_GREEN + "Cleaning files! Check console for more information.");
                instance.cleanFiles();
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
