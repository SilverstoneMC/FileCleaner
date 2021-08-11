package me.jasonhorkles.filecleanerspigot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class Commands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args[0].toLowerCase()) {
            case "reload":
                new FCSpigot().saveDefaultConfig();
                new FCSpigot().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "FileCleaner reloaded!");
                return true;
            case "cleannow":
                if (sender instanceof Player)
                    sender.sendMessage(ChatColor.DARK_GREEN + "Cleaning files! Check console for more information.");
                new FCSpigot().cleanFiles();
                return true;
            default:
                return false;
        }
    }
}
