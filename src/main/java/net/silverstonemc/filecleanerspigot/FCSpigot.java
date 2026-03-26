/*
 * FileCleaner - a Minecraft plugin
 * Copyright (C) 2026 JasonHorkles and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package net.silverstonemc.filecleanerspigot;

import net.silverstonemc.filecleaner.CleanFiles;
import net.silverstonemc.filecleaner.VersionChecker;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@SuppressWarnings("unused")
public class FCSpigot extends JavaPlugin {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        //noinspection ResultOfObjectAllocationIgnored
        new Metrics(this, 30400);

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

        cleanFiles(Bukkit.getConsoleSender());
    }

    @SuppressWarnings("DataFlowIssue")
    public void cleanFiles(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "[FileCleaner] Starting file cleaning task...");
        CleanFiles cleanFiles = new CleanFiles();

        // Clean directories
        for (String key : getConfig().getConfigurationSection("folders").getKeys(false)) {
            String folder = getConfig().getString("folders." + key + ".location");

            if (folder == null) continue;

            int age = getConfig().getInt("folders." + key + ".age");
            int count = getConfig().getInt("folders." + key + ".count");
            long size = getConfig().getLong("folders." + key + ".size");
            List<String> excludedFiles = getConfig().getStringList("folders." + key + ".exclude");

            cleanFiles.scanFilesInDir(folder, getLogger(), age, count, size, excludedFiles);
        }

        // Clean individual files
        for (String key : getConfig().getConfigurationSection("files").getKeys(false)) {
            String file = getConfig().getString("files." + key + ".location");

            if (file == null) continue;

            int age = getConfig().getInt("files." + key + ".age");
            long size = getConfig().getLong("files." + key + ".size");

            cleanFiles.scanFile(file, getLogger(), age, size);
        }

        String s = CleanFiles.filesDeleted == 1 ? "" : "s";
        sender.sendMessage(ChatColor.DARK_GREEN + "[FileCleaner] Done! " + CleanFiles.filesDeleted + " file" + s + " deleted, saving " + CleanFiles.mbSaved + " MB");

        CleanFiles.filesDeleted = 0;
        CleanFiles.mbSaved = 0;
    }
}
