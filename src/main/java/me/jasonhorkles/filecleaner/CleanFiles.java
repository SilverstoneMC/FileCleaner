package me.jasonhorkles.filecleaner;

import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class CleanFiles {

    public void CleanFilesTask(String folderName, JavaPlugin sPlugin, Plugin bPlugin, int age, int count) {
        File folder = new File(folderName);
        try {
            if (age > -1) {
                //noinspection ConstantConditions
                for (File file : folder.listFiles()) {
                    long diff = new Date().getTime() - file.lastModified();

                    if (diff > (long) age * 24 * 60 * 60 * 1000) {
                        deleteFile(sPlugin, bPlugin, file);
                    }
                }
            }

            if (count > -1) {
                File[] files = folder.listFiles();
                //noinspection ConstantConditions
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                for (int x = 0; x < files.length - count; x++) {
                    deleteFile(sPlugin, bPlugin, files[x]);
                }
            }
        } catch (NullPointerException e) {
            if (sPlugin == null) {
                bPlugin.getLogger()
                        .severe("Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.");
            } else {
                sPlugin.getLogger()
                        .severe("Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.");
            }
        }
    }

    public void deleteFile(JavaPlugin sPlugin, Plugin bPlugin, File file) {
        if (file.delete()) {
            if (sPlugin == null) {
                bPlugin.getLogger().info("Successfully deleted file \"" + file.getPath() + "\"");
            } else {
                sPlugin.getLogger().info("Successfully deleted file \"" + file.getPath() + "\"");
            }
        } else {
            if (sPlugin == null) {
                bPlugin.getLogger()
                        .severe("Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!");
            } else {
                sPlugin.getLogger()
                        .severe("Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!");
            }
        }
    }
}
