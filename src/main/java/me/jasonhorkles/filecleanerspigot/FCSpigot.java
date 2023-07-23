package me.jasonhorkles.filecleanerspigot;

import me.jasonhorkles.filecleaner.CleanFiles;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class FCSpigot extends JavaPlugin {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        getCommand("filecleaner").setExecutor(new Commands(this, this));
        getCommand("filecleaner").setTabCompleter(new TabComplete());

        saveDefaultConfig();

        cleanFiles();
    }

    @SuppressWarnings("DataFlowIssue")
    public void cleanFiles() {
        getLogger().info("Starting file cleaning task...");
        for (String folders : getConfig().getConfigurationSection("folders").getKeys(false)) {
            String folder = getConfig().getString("folders." + folders + ".location");

            if (folder == null) continue;

            int age = getConfig().getInt("folders." + folders + ".age");
            int count = getConfig().getInt("folders." + folders + ".count");
            long size = getConfig().getLong("folders." + folders + ".size");
            new CleanFiles().CleanFilesTask(folder, getLogger(), age, count, size);
        }
        getLogger().info("Done!");
    }
}
