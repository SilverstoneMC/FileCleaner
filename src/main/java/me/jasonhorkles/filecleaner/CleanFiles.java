package me.jasonhorkles.filecleaner;

import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class CleanFiles {

    public void CleanFilesTask(String fileName, JavaPlugin sPlugin, Plugin bPlugin, int age, int count) {
        File file = new File(fileName);
        try {
            if (age > -1) {
                //noinspection ConstantConditions
                for (File f : file.listFiles()) {
                    long diff = new Date().getTime() - f.lastModified();

                    if (diff > (long) age * 24 * 60 * 60 * 1000) {
                        deleteFile(sPlugin, bPlugin, f);
                    }
                }
            }

            if (count > -1) {
                File[] files = file.listFiles();
                //noinspection ConstantConditions
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                for (int x = 0; x < files.length - count; x++) {
                    deleteFile(sPlugin, bPlugin, files[x]);
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

    public void deleteFile(JavaPlugin sPlugin, Plugin bPlugin, File f) {
        if (f.delete()) {
            if (sPlugin == null) {
                bPlugin.getLogger().info("Successfully deleted file \"" + f.getPath() + "\"");
            } else {
                sPlugin.getLogger().info("Successfully deleted file \"" + f.getPath() + "\"");
            }
        } else {
            if (sPlugin == null) {
                bPlugin.getLogger().severe("Couldn't delete file \"" + f.getPath() + "\" - make sure it's not currently in use!");
            } else {
                sPlugin.getLogger().severe("Couldn't delete file \"" + f.getPath() + "\" - make sure it's not currently in use!");
            }
        }
    }
}
