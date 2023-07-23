package me.jasonhorkles.filecleaner;

import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CleanFiles {

    public void CleanFilesTask(String folderName, JavaPlugin sPlugin, Plugin bPlugin, com.velocitypowered.api.plugin.Plugin vPlugin, int age, int count, long size) {
        File folder = new File("." + folderName);
        try {
            //noinspection DataFlowIssue
            List<File> files = Arrays.stream(folder.listFiles()).collect(Collectors.toList());

            if (files.size() == 0) return;

            if (age > -1) {
                ArrayList<File> filesToRemove = new ArrayList<>();
                for (File file : files) {
                    long diff = new Date().getTime() - file.lastModified();

                    if (diff > (long) age * 24 * 60 * 60 * 1000) {
                        deleteFile(sPlugin, bPlugin, file);
                        filesToRemove.add(file);
                    }
                }
                files.removeAll(filesToRemove);
            }

            if (count > -1) {
                ArrayList<File> filesToRemove = new ArrayList<>();
                files.sort(Comparator.comparingLong(File::lastModified));
                for (int x = 0; x < files.size() - count; x++) {
                    deleteFile(sPlugin, bPlugin, files.get(x));
                    filesToRemove.add(files.get(x));
                }
                files.removeAll(filesToRemove);
            }

            if (size > -1) {
                files.sort(Comparator.comparingLong(File::length));
                for (File file : files)
                    if (Math.round(file.length() / 1024.0) > (double) size)
                        deleteFile(sPlugin, bPlugin, file);
            }
        } catch (NullPointerException e) {
            if (sPlugin == null) bPlugin.getLogger().severe(
                "Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.");
            else sPlugin.getLogger().severe(
                "Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.");
        }
    }

    public void deleteFile(JavaPlugin sPlugin, Plugin bPlugin, File file) {
        if (file.delete()) if (sPlugin == null)
            bPlugin.getLogger().info("Successfully deleted file \"" + file.getPath() + "\"");
        else sPlugin.getLogger().info("Successfully deleted file \"" + file.getPath() + "\"");
        else if (sPlugin == null) bPlugin.getLogger()
            .severe("Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!");
        else sPlugin.getLogger().severe(
                "Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!");
    }
}
