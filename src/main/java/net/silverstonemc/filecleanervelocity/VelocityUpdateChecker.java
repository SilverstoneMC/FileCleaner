package net.silverstonemc.filecleanervelocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.filecleaner.VersionChecker;

public class VelocityUpdateChecker {
    public VelocityUpdateChecker(FCVelocity instance) {
        i = instance;
    }

    private final FCVelocity i;
    public static final String pluginName = "FileCleaner";

    @Subscribe
    public void onJoin(ServerConnectedEvent event) {
        if (event.getPlayer().hasPermission(pluginName.toLowerCase() + ".updatenotifs"))
            // Check for updates asynchronously
            i.server.getScheduler().buildTask(
                i, () -> {
                    String latest = new VersionChecker().getLatestVersion();
                    //noinspection OptionalGetWithoutIsPresent because it should exist
                    String current = i.server.getPluginManager().getPlugin(pluginName.toLowerCase()).get()
                        .getDescription().getVersion().get().replace("v", "");

                    if (latest == null) return;
                    if (!current.equals(latest)) event.getPlayer().sendMessage(Component.text("An update is available for " + pluginName + "! ",
                            NamedTextColor.YELLOW)
                        .append(Component.text("(" + current + " → " + latest + ")\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "https://github.com/SilverstoneMC/" + pluginName + "/releases/latest",
                                NamedTextColor.DARK_AQUA)
                            .clickEvent(ClickEvent.openUrl("https://github.com/SilverstoneMC/" + pluginName + "/releases/latest"))));
                }).schedule();
    }

    public void logUpdate(String current, String latest) {
        i.logger.warn("An update is available for " + pluginName + "! ({} → {})", current, latest);
        i.logger.warn("https://github.com/SilverstoneMC/" + pluginName + "/releases/latest");
    }
}
