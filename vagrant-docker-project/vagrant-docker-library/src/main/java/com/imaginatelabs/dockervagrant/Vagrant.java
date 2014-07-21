package com.imaginatelabs.dockervagrant;


import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Vagrant {

    public static final String DESTROY = "destroy";
    public static final String HALT = "halt";
    public static final String SUSPEND = "suspend";

    public static void up(VagrantFile vagrantFile) {
        if (cmd("up", vagrantFile.getPath(), "Calling vagrant up") == 0) {
            LogF.i("Docker now running on Vagrant");
        } else {
            LogF.e("There was an error starting Docker in Vagrant");
        }
    }

    public static boolean isInstalled() {
        if (cmd("-v", new File("/").toPath(), "Running Docker on Vagrant") != 0) {
            LogF.e("An error occurred, stopping Docker on Vagrant");
            return false;
        }
        return true;
    }

    public static int destroy(Path dir) {
        return cmd(DESTROY + " -f", dir, "Destroying running instance Docker and Vagrant");
    }

    public static int halt(Path dir) {
        return cmd(HALT, dir, "Halting running instance Docker and Vagrant");
    }

    public static int suspend(Path dir) {
        return cmd(SUSPEND, dir, "Suspending running instance Docker and Vagrant");
    }

    public static int cmd(String arg, Path dir, String message) {
        LogF.i(message);
        int exit = -1;
        try {
            exit = com.imaginatelabs.dockervagrant.Console.exec(vagrant(arg), null, dir.toFile());
        } catch (Exception e) {
            LogF.e(e.getMessage());
        }
        return exit;
    }

    private static String vagrant(String cmd) {
        return String.format("vagrant %s", cmd);
    }

    public static void ssh(String containerName) {
        //if containerName is empty just ssh into the vagrant box
        //else vagrant ssh then docker connect to image
        //Connect to the console so that commands can be entered,
        //NOTE: It would be good to return this console session so
        //      that it can be used by other processes
        cmd("ssh", new File("/").toPath(), "Connecting to container " + containerName);
    }

    public static void packageBox(String boxName, Path dir) {
        cmd("package --output " + boxName, dir, "Creating Box with name " + boxName);
    }

    public static void run(VagrantFile vagrantFile, boolean cacheConfiguration) throws IOException, MojoExecutionException {
        if (!Vagrant.isInstalled()) return;

        boolean hasPreviousVagrantHashFile = doesPreviousVagrantHashFileExist(vagrantFile);
        boolean hasEqualHashesForCurrentAndPrevious = false;
        String currentHash = vagrantFile.getHexHashCode();


        if (hasPreviousVagrantHashFile) {
            String previousHash = readHashFromFile(vagrantFile);
            hasEqualHashesForCurrentAndPrevious = StringUtils.equals(currentHash, previousHash);

            if (hasEqualHashesForCurrentAndPrevious && Vagrant.hasExistingBox(currentHash)) {
                LogF.i("Current configuration already cached in box %s", currentHash);
                LogF.i("Using cached configuration in box %s", currentHash);
                vagrantFile.useCachedBox(currentHash);
            } else if (Vagrant.hasExistingBox(previousHash)) {
                LogF.i("Removing previous cached configuration in box %s", previousHash);
                Vagrant.removeBox(previousHash);
            }
        } else {
            LogF.i("No Vagrantfile.hash exists creating Vagrantfile from docker-maven-plugin configuration.");
        }

        if (shouldWriteVagrantHashFileToSystem(cacheConfiguration, hasPreviousVagrantHashFile, hasEqualHashesForCurrentAndPrevious)) {
            Vagrant.writeVagrantHashFileToTheFileSystem(vagrantFile);
        }

        Vagrant.writeVagrantFileToTheFileSystem(vagrantFile);
        Vagrant.up(vagrantFile);

        if (shouldCacheBox(cacheConfiguration, hasEqualHashesForCurrentAndPrevious)) {
            cacheBox(vagrantFile, currentHash);
        }
    }

    private static boolean doesPreviousVagrantHashFileExist(VagrantFile vagrantFile) {
        LogF.i(String.format("Checking for existing file %s", vagrantFile.getFullHashFilePath()));
        boolean hasExistingFile = false;
        if (hasExistingFile = Files.exists(vagrantFile.getFullHashFilePath())) {
            LogF.i(String.format("File exists at %s", vagrantFile.getFullHashFilePath()));
        }
        return hasExistingFile;
    }

    private static boolean shouldCacheBox(boolean cacheConfiguration, boolean hasEqualHashesForCurrentAndPrevious) {
        return cacheConfiguration && !hasEqualHashesForCurrentAndPrevious;
    }

    private static boolean shouldWriteVagrantHashFileToSystem(boolean cacheConfiguration, boolean hasPreviousVagrantHashFile, boolean hasEqualHashesForCurrentAndPrevious) {
        return cacheConfiguration && (!hasPreviousVagrantHashFile || !hasEqualHashesForCurrentAndPrevious);
    }

    private static void cacheBox(VagrantFile vagrantFile, String hash) throws IOException {
        LogF.i("Caching current configuration as box: %s", hash);
        Vagrant.packageBox(hash, vagrantFile.getPath());
        Vagrant.addBox(hash, vagrantFile);
        Vagrant.up(vagrantFile);
        cleanupTempPackagedBox(vagrantFile);
    }

    private static void cleanupTempPackagedBox(VagrantFile vagrantFile) throws IOException {
        File file = new File(vagrantFile.getPath().toString(), vagrantFile.getHexHashCode());

        LogF.i("Cleaning up - removing %s", file.getAbsolutePath());
        if (Files.deleteIfExists(file.toPath())) {
            LogF.i("Successfully removed %s", file.getAbsolutePath());
        } else {
            LogF.i("Failed to removed %s", file.getAbsolutePath());
        }
    }

    private static void addBox(String hash, VagrantFile vagrantFile) {
        cmd(String.format("box add --name %s %s", hash, hash), vagrantFile.getPath(), "");
    }

    private static void removeBox(String boxName) {
        cmd("box remove " + boxName, new File("/").toPath(), "Removing Box with name " + boxName);
    }

    private static boolean hasExistingBox(String hash) {
        List<String> output = new ArrayList<String>();
        try {
            com.imaginatelabs.dockervagrant.Console.exec("vagrant box list", null, new File("/"), output);
            for (String line : output) {
                if (StringUtils.contains(line, hash)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void writeVagrantHashFileToTheFileSystem(VagrantFile vagrantFile) throws IOException {
        writeFileToTheFileSystem(vagrantFile.getFullHashFilePath(), vagrantFile.getHexHashCode());
    }

    public static void writeVagrantFileToTheFileSystem(VagrantFile vagrantFile) throws IOException {
        writeFileToTheFileSystem(vagrantFile.getFullPath(), vagrantFile.toText());
    }

    public static void writeFileToTheFileSystem(Path absolutePath, String content) throws IOException {
        LogF.i("Writing %s", absolutePath);
        if (!Files.exists(absolutePath.getParent())) {
            Files.createDirectory(absolutePath.getParent());
        }
        Files.write(absolutePath, content.getBytes());
    }

    private static String readHashFromFile(VagrantFile vagrantFile) throws IOException {
        return new String(Files.readAllBytes(vagrantFile.getFullHashFilePath()));
    }
}
