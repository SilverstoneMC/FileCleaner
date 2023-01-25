package me.jasonhorkles.filecleanerbungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class Commands extends Command implements TabExecutor {

    private final FCBungee instance;

    public Commands(FCBungee instance) {
        super("filecleanerbungee", "filecleaner.command", "fcb");
        this.instance = instance;
    }


    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(
                ChatColor.RED + "Usage: /filecleanerbungee <reload | cleannow>"));
            return;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("filecleaner.reload")) {
            instance.loadConfig();
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "FileCleaner reloaded!"));

        } else if (args[0].equalsIgnoreCase("cleannow") && sender.hasPermission("filecleaner.cleannow")) {
            if (sender instanceof ProxiedPlayer) sender.sendMessage(new TextComponent(
                ChatColor.DARK_GREEN + "Cleaning files! Check console for more information."));
            instance.cleanFiles();

        } else sender.sendMessage(
            TextComponent.fromLegacyText(ChatColor.RED + "Usage: /filecleanerbungee <reload | cleannow>"));
    }


    final List<String> arguments = new ArrayList<>();

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("filecleaner.reload")) {
            if (!arguments.contains("reload")) arguments.add("reload");
        } else arguments.remove("reload");

        if (sender.hasPermission("filecleaner.cleannow")) {
            if (!arguments.contains("cleannow")) arguments.add("cleannow");
        } else arguments.remove("cleannow");

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
            return result;
        }
        return new ArrayList<>();
    }
}
