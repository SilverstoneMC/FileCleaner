package net.silverstonemc.filecleanervelocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Commands implements SimpleCommand {
    public Commands(FCVelocity instance) {
        this.instance = instance;
    }

    private final FCVelocity instance;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /filecleanervelocity <reload | cleannow>",
                NamedTextColor.RED));
            return;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("filecleaner.reload")) {
            instance.loadConfig();
            sender.sendMessage(Component.text("FileCleaner (Velocity) reloaded!", NamedTextColor.GREEN));

        } else if (args[0].equalsIgnoreCase("cleannow") && sender.hasPermission("filecleaner.cleannow")) {
            if (sender instanceof Player) sender.sendMessage(Component.text("Cleaning files! Check console for more information.",
                NamedTextColor.DARK_GREEN));
            instance.cleanFiles();

        } else sender.sendMessage(Component.text("Usage: /filecleanervelocity <reload | cleannow>",
            NamedTextColor.RED));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("filecleaner.command");
    }


    final List<String> arguments = new ArrayList<>();

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (sender.hasPermission("filecleaner.reload")) {
            if (!arguments.contains("reload")) arguments.add("reload");
        } else arguments.remove("reload");

        if (sender.hasPermission("filecleaner.cleannow")) {
            if (!arguments.contains("cleannow")) arguments.add("cleannow");
        } else arguments.remove("cleannow");

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
            return CompletableFuture.completedFuture(result);
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}
