package com.imageinatelabs;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "ssh")
public class SshDocker extends AbstractMojo {

    @Parameter( property = "outputDir", defaultValue = "${project.build.directory}",  required = true )
    private File outputDirectory;

    @Parameter( property = "ssh.containerName")
    private String containerName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //TODO ssh into vagrant box and then attach to the docker container in interactive mode
        Vagrant.ssh(containerName);
    }
}
