package com.imageinatelabs;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.List;

@Mojo( name = "run")
public class RunDocker extends AbstractMojo {

    //TODO create vagrant file in root of the project so that it has access to all the project files
    @Parameter( property = "outputDir", defaultValue = "${project.build.directory}",  required = true )
    private File outputDirectory;

    @Parameter( property = "run.box", defaultValue = "precise64" )
    private String box;

    @Parameter( property = "run.boxUrl", defaultValue = "http://files.vagrantup.com/precise64.box" )
    private String boxUrl;

    @Parameter( property = "run.network", defaultValue = "" )
    private String network;

    @Parameter( property = "run.containers" )
    private List<Container> containers;

    private Log log = getLog();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(Vagrant.isInstalled(log)) {
            VagrantFile vagrantFile = compileVagrantDockerConfig();
            Vagrant.up(vagrantFile, log);
        }
    }

    private VagrantFile compileVagrantDockerConfig() throws MojoExecutionException {
        VagrantFile vagrantFile = new VagrantFile(outputDirectory, box, boxUrl, network, containers);
        if(Vagrant.hasVagrantFileConfigurationChanged(vagrantFile)) {
            log.info(String.format("Writing Docker and Vagrant config to %s", "foobar"));
            Vagrant.writeVagrantFileToTheFileSystem(vagrantFile);
        }else{
            log.info(String.format("No changes made for Docker and Vagrant config in %s", vagrantFile.getFullPath().toString()));
        }
        return vagrantFile;
    }

}
