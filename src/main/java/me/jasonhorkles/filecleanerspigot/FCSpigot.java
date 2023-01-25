package me.jasonhorkles.filecleanerspigot;

import me.jasonhorkles.filecleaner.CleanFiles;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class FCSpigot extends JavaPlugin {

    private FCSpigot instance;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        instance = this;

        getCommand("filecleaner").setExecutor(new Commands(this, instance));
        getCommand("filecleaner").setTabCompleter(new TabComplete());

        saveDefaultConfig();

        cleanFiles();
    }

    @SuppressWarnings("DataFlowIssue")
    public void cleanFiles() {
        instance.getLogger().info("Starting file cleaning task...");
        for (String folders : instance.getConfig().getConfigurationSection("folders").getKeys(false)) {
            String folder = instance.getConfig().getString("folders." + folders + ".location");

            if (folder == null) continue;

            int age = instance.getConfig().getInt("folders." + folders + ".age");
            int count = instance.getConfig().getInt("folders." + folders + ".count");
            long size = instance.getConfig().getLong("folders." + folders + ".size");
            new CleanFiles().CleanFilesTask(folder, instance, null, age, count, size);
        }
        instance.getLogger().info("Done!");
    }
}
