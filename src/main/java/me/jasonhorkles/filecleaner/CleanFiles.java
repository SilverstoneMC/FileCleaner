package me.jasonhorkles.filecleaner;


import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CleanFiles {

    public void CleanFilesTask(String folderName, Object logger, int age, int count, long size) {
        File folder = new File("." + folderName);
        try {
            //noinspection DataFlowIssue
            List<File> files = Arrays.stream(folder.listFiles()).collect(Collectors.toList());

            if (files.isEmpty()) return;

            if (age > -1) {
                ArrayList<File> filesToRemove = new ArrayList<>();
                for (File file : files) {
                    long diff = new Date().getTime() - file.lastModified();

                    if (diff > (long) age * 24 * 60 * 60 * 1000) {
                        deleteFile(logger, file);
                        filesToRemove.add(file);
                    }
                }
                files.removeAll(filesToRemove);
            }

            if (count > -1) {
                ArrayList<File> filesToRemove = new ArrayList<>();
                files.sort(Comparator.comparingLong(File::lastModified));
                for (int x = 0; x < files.size() - count; x++) {
                    deleteFile(logger, files.get(x));
                    filesToRemove.add(files.get(x));
                }
                files.removeAll(filesToRemove);
            }

            if (size > -1) {
                files.sort(Comparator.comparingLong(File::length));
                for (File file : files)
                    if (Math.round(file.length() / 1024.0) > (double) size) deleteFile(logger, file);
            }
        } catch (NullPointerException e) {
            log(logger,
                "Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.",
                LogLevel.SEVERE);
        }
    }

    public void deleteFile(Object logger, File file) {
        if (file.delete()) log(logger, "Successfully deleted file \"" + file.getPath() + "\"", LogLevel.INFO);
        else log(logger,
            "Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!",
            LogLevel.SEVERE);
    }

    private enum LogLevel {
        INFO, SEVERE
    }

    private void log(Object logger, String message, LogLevel logLevel) {
        if (logger instanceof Logger newLogger) switch (logLevel) {
            case INFO -> newLogger.info(message);
            case SEVERE -> newLogger.severe(message);
        }

        if (logger instanceof org.slf4j.Logger newLogger) switch (logLevel) {
            case INFO -> newLogger.info(message);
            case SEVERE -> newLogger.error(message);
        }
    }
}
