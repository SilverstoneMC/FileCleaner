package net.silverstonemc.filecleanerbungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.silverstonemc.filecleaner.VersionChecker;

import java.util.concurrent.TimeUnit;

public class BungeeUpdateChecker implements Listener {
    public BungeeUpdateChecker(Plugin plugin) {
        this.plugin = plugin;
    }

    private final Plugin plugin;

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        String pluginName = plugin.getDescription().getName();

        if (event.getPlayer().hasPermission(pluginName.toLowerCase() + ".updatenotifs"))
            // Check for updates asynchronously
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                String latest = new VersionChecker().getLatestVersion();
                String current = plugin.getDescription().getVersion().replace("b", "");

                if (latest == null) return;
                if (!current.equals(latest)) event.getPlayer().sendMessage(new ComponentBuilder(
                    "An update is available for " + pluginName + "! ").color(ChatColor.YELLOW)
                    .append("(" + current + " → " + latest + ")\n").color(ChatColor.GOLD).append(
                        "https://github.com/SilverstoneMC/" + pluginName + "/releases/latest")
                    .color(ChatColor.DARK_AQUA).create());
            }, 0L, TimeUnit.MILLISECONDS);
    }

    public void logUpdate(String current, String latest) {
        String pluginName = plugin.getDescription().getName();

        plugin.getLogger()
            .warning("An update is available for " + pluginName + "! (" + current + " → " + latest + ")");
        plugin.getLogger().warning("https://github.com/SilverstoneMC/" + pluginName + "/releases/latest");
    }
}
