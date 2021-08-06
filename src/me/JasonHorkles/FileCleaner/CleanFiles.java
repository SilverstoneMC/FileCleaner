package me.JasonHorkles.FileCleaner;

import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Date;

public class CleanFiles {

    public static void CleanFilesTask(String fileName, JavaPlugin sPlugin, Plugin bPlugin, int days) {
        File file = new File(fileName);
        try {
            //noinspection ConstantConditions
            for (File f : file.listFiles()) {
                long diff = new Date().getTime() - f.lastModified();

                if (diff > (long) days * 24 * 60 * 60 * 1000) {
                    if (f.delete()) {
                        if (sPlugin == null) {
                            bPlugin.getLogger().info("Successfully deleted file \"" + f.getPath() + "\"");
                        } else {
                            sPlugin.getLogger().info("Successfully deleted file \"" + f.getPath() + "\"");
                        }
                    } else {
                        if (sPlugin == null) {
                            bPlugin.getLogger().severe("Couldn't delete file \"" + f.getPath() + "\"");
                        } else {
                            sPlugin.getLogger().severe("Couldn't delete file \"" + f.getPath() + "\"");
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            if (sPlugin == null) {
                bPlugin.getLogger()
                        .severe("Couldn't find the folder \"" + file.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.");
            } else {
                sPlugin.getLogger()
                        .severe("Couldn't find the folder \"" + file.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.");
            }
        }
    }
}
