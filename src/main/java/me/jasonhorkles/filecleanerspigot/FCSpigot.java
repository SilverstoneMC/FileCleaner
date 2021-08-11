package me.jasonhorkles.filecleanerspigot;

import me.jasonhorkles.filecleaner.CleanFiles;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class FCSpigot extends JavaPlugin {

    private FCSpigot instance;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        instance = this;

        getCommand("filecleaner").setExecutor(new Commands());
        getCommand("filecleaner").setTabCompleter(new TabComplete());

        saveDefaultConfig();

        getLogger().info("Plugin location: \"" + instance.getFile().getAbsolutePath() + "\"");

        cleanFiles();
    }

    @SuppressWarnings("ConstantConditions")
    public void cleanFiles() {
        instance.getLogger().info("Starting file cleaning task...");
        for (String folders : instance.getConfig().getConfigurationSection("folders").getKeys(false)) {
            String folder = instance.getConfig().getString("folders." + folders + ".location");

            if (folder == null) continue;

            int age = instance.getConfig().getInt("folders." + folders + ".age");
            int count = instance.getConfig().getInt("folders." + folders + ".count");
            new CleanFiles().CleanFilesTask(folder, instance, null, age, count);
        }
        instance.getLogger().info("Done!");
    }
}
