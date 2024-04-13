package net.silverstonemc.filecleanerspigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    final List<String> arguments = new ArrayList<>();

    public List<String> onTabComplete(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
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
        return null;
    }
}