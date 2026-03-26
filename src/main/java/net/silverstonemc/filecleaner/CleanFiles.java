// Copyright JasonHorkles and contributors
// SPDX-License-Identifier: GPL-3.0-or-later
package net.silverstonemc.filecleaner;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class CleanFiles {
    public static int filesDeleted;
    public static double mbSaved;

    @SuppressWarnings("MethodWithTooManyParameters")
    public void scanFilesInDir(String folderName, Object logger, int age, int count, long size, List<String> excludedFiles) {
        File folder = new File("." + folderName);
        if (folder.listFiles() == null) {
            log(
                logger,
                "Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.",
                LogLevel.SEVERE);
            return;
        }
        //noinspection DataFlowIssue
        List<File> fileList = Arrays.stream(folder.listFiles()).collect(Collectors.toList());

        if (fileList.isEmpty()) return;

        // Remove excluded files from the list of files to check
        if (excludedFiles != null && !excludedFiles.isEmpty()) {
            List<File> filesToRemove = new ArrayList<>();
            Map<String, Pattern> compiledPatterns = new HashMap<>();
            Set<String> invalidPatterns = new HashSet<>();

            for (File file : fileList) {
                boolean excluded = false;

                for (String excludedRule : excludedFiles) {
                    // Preserve exact-match behavior for those that don't use regex
                    if (excludedRule.equals(file.getName())) {
                        excluded = true;
                        break;
                    }

                    Pattern pattern = compiledPatterns.get(excludedRule);
                    if (pattern == null && !invalidPatterns.contains(excludedRule)) try {
                        pattern = Pattern.compile(excludedRule);
                        compiledPatterns.put(excludedRule, pattern);
                    } catch (PatternSyntaxException ex) {
                        invalidPatterns.add(excludedRule);
                        log(logger, "Invalid excluded file regex: \"" + excludedRule + "\"", LogLevel.SEVERE);
                    }

                    if (pattern != null && pattern.matcher(file.getName()).matches()) {
                        excluded = true;
                        break;
                    }
                }

                if (excluded) {
                    log(
                        logger,
                        "Skipping file \"" + file.getPath() + "\" based on config exclusions",
                        LogLevel.INFO);
                    filesToRemove.add(file);
                }
            }
            fileList.removeAll(filesToRemove);
        }

        // Check age
        if (age > -1) {
            List<File> filesToRemove = new ArrayList<>();
            for (File file : fileList) {
                long diff = new Date().getTime() - file.lastModified();

                if (diff > age * 24L * 60L * 60L * 1000L) {
                    deleteFile(logger, file);
                    // Add files to arraylist to avoid them being removed again below
                    filesToRemove.add(file);
                }
            }
            fileList.removeAll(filesToRemove);
        }

        // Check count
        if (count > -1) {
            List<File> filesToRemove = new ArrayList<>();
            fileList.sort(Comparator.comparingLong(File::lastModified));
            for (int x = 0; x < fileList.size() - count; x++) {
                deleteFile(logger, fileList.get(x));
                // Add files to arraylist to avoid them being removed again below
                filesToRemove.add(fileList.get(x));
            }
            fileList.removeAll(filesToRemove);
        }

        // Check size
        if (size > -1) {
            fileList.sort(Comparator.comparingLong(File::length));
            for (File file : fileList)
                if (Math.round(file.length() / 1024.0) > (double) size) deleteFile(logger, file);
        }
    }

    /**
     * Scans and deletes singular files defined in the config.
     */
    public void scanFile(String fileName, Object logger, int age, long size) {
        File file = new File("." + fileName);
        if (!file.exists()) return;

        if (age > -1) {
            long diff = new Date().getTime() - file.lastModified();

            if (diff > age * 24L * 60L * 60L * 1000L) {
                deleteFile(logger, file);
                return;
            }
        }

        if (size > -1) if (Math.round(file.length() / 1024.0) > (double) size) deleteFile(logger, file);
    }

    public void deleteFile(Object logger, File file) {
        // Make sure the file is actually a file and not a directory before trying to delete it
        if (!file.isFile()) return;

        long fileSize = file.length();

        if (file.delete()) {
            log(logger, "Successfully deleted file \"" + file.getPath() + "\"", LogLevel.INFO);
            filesDeleted++;
            mbSaved += Math.round((fileSize / (1024.0 * 1024.0)) * 100.0) / 100.0;

        } else log(
            logger,
            "Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!",
            LogLevel.SEVERE);
    }

    private enum LogLevel {
        INFO,
        SEVERE
    }

    private void log(Object logger, String message, LogLevel logLevel) {
        // Used by Spigot
        if (logger instanceof Logger newLogger) switch (logLevel) {
            case INFO -> newLogger.info(message);
            case SEVERE -> newLogger.severe(message);
        }

        // Used by Velocity
        if (logger instanceof org.slf4j.Logger newLogger) switch (logLevel) {
            case INFO -> newLogger.info(message);
            case SEVERE -> newLogger.error(message);
        }
    }
}
