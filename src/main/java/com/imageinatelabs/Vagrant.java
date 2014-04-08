package com.imageinatelabs;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.nio.file.Files;

public class Vagrant {

    public static final String DESTROY = "destroy";
    public static final String HALT = "halt";
    public static final String SUSPEND = "suspend";

    static void up(VagrantFile vagrantFile, Log log){
        if(cmd("up", new File(vagrantFile.getPath().toString()),"Calling vagrant up", log) == 0) {
            log.info("Docker now running on Vagrant");
        }else{
            log.error("There was an error starting Docker in Vagrant");
        }
    }

    static boolean isInstalled(Log log){
        if(cmd("-v",new File("/"),"Running Docker on Vagrant",log)!=0){
            log.error("An error occurred, stopping Docker on Vagrant");
            return false;
        }
        return true;
    }

    static int destroy(File vagrantFileDirectory, Log log) {
        return cmd(DESTROY+" -f",vagrantFileDirectory,"Destroying running instance Docker and Vagrant",log);
    }

    public static int halt(File vagrantFileDirectory, Log log) {
        return cmd(HALT,vagrantFileDirectory,"Halting running instance Docker and Vagrant",log);
    }

    public static int suspend(File vagrantFileDirectory, Log log){
        return cmd(SUSPEND,vagrantFileDirectory,"Suspending running instance Docker and Vagrant",log);
    }

    public static int cmd(String arg, File vagrantFileDirectory, String message, Log log) {
        log.info(message);
        int exit = -1;
        try {
            exit = Console.exec(vagrant(arg), null, vagrantFileDirectory, log);
        } catch (Exception e) {
            log.error(e);
        }
        return exit;
    }

    private static String vagrant(String cmd) {
        return String.format("vagrant %s",cmd);
    }

    public static void ssh(String containerName, File vagrantFileDirectory, Log log) {
        //if containerName is empty just ssh into the vagrant box
        //else vagrant ssh then docker connect to image
        //Connect to the console so that commands can be entered,
        //NOTE: It would be good to return this console session so
        //      that it can be used by other processes
        cmd("ssh",vagrantFileDirectory,"Connecting to container "+containerName,log);
    }

    public static void pack(){
        //TODO Creates vagrant box which is used if config hasn't changed
        //noPack = true :means that it will create a box each time.
        //This will check the Vagrantfile and see if a box exists with the name that is a hash of the Vagrantfile
    }

    public static void writeVagrantFileToTheFileSystem(VagrantFile vagrantFile) throws MojoExecutionException {
        try{
            if(!Files.exists(vagrantFile.getPath())) {
                Files.createDirectory(vagrantFile.getPath());
            }
            Files.write(vagrantFile.getFullPath(), vagrantFile.getBytes());
        }catch ( IOException e ){
            throw new MojoExecutionException("Error creating Vagrantfile", e);
        }
    }

    public static boolean hasVagrantFileConfigurationChanged(VagrantFile vagrantFile){
        try {
            return !StringUtils.equals(
                    readVagrantFileContent(vagrantFile),
                    vagrantFile.toFileFormat());
        } catch (IOException e) { /*Do Nothing*/ }
        return true;
    }

    private static String readVagrantFileContent(VagrantFile fullPath) throws IOException {
        return new String(Files.readAllBytes(fullPath.getFullPath()));
    }
}
