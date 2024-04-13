package net.silverstonemc.filecleanerspigot;

import net.silverstonemc.filecleaner.CleanFiles;
import net.silverstonemc.filecleaner.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("unused")
public class FCSpigot extends JavaPlugin {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        getCommand("filecleaner").setExecutor(new Commands(this, this));
        getCommand("filecleaner").setTabCompleter(new TabComplete());

        getServer().getPluginManager().registerEvents(new SpigotUpdateChecker(this), this);

        saveDefaultConfig();

        // Log version update
        FCSpigot instance = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                String latest = new VersionChecker().getLatestVersion();
                String current = instance.getDescription().getVersion().replace("s", "");

                if (latest == null) return;
                if (!current.equals(latest)) new SpigotUpdateChecker(instance).logUpdate(current, latest);
            }
        }.runTaskLaterAsynchronously(this, 10L);

        cleanFiles();
    }

    @SuppressWarnings("DataFlowIssue")
    public void cleanFiles() {
        getLogger().info("Starting file cleaning task...");
        CleanFiles cleanFiles = new CleanFiles();
        for (String folders : getConfig().getConfigurationSection("folders").getKeys(false)) {
            String folder = getConfig().getString("folders." + folders + ".location");

            if (folder == null) continue;

            int age = getConfig().getInt("folders." + folders + ".age");
            int count = getConfig().getInt("folders." + folders + ".count");
            long size = getConfig().getLong("folders." + folders + ".size");
            cleanFiles.CleanFilesTask(folder, getLogger(), age, count, size);
        }
        getLogger().info("Done!");
    }
}
