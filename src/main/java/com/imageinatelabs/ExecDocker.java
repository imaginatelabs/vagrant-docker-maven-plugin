package com.imageinatelabs;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "exec")
public class ExecDocker extends AbstractMojo {

    @Parameter( property = "outputDir", defaultValue = "${project.build.directory}",  required = true )
    private File outputDirectory;

    @Parameter( property = "exec.containerName")
    private String containerName;

    @Parameter( property = "exec.cmd")
    private String cmd;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //TODO SSH into Vagrant attach to the docker container, this will require processes are forked to exec
    }
}
