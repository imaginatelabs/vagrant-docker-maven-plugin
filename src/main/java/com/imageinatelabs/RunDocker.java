package com.imageinatelabs;



import com.imaginatelabs.dockervagrant.Container;
import com.imaginatelabs.dockervagrant.Vagrant;
import com.imaginatelabs.dockervagrant.VagrantFile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.List;

@Mojo( name = "run")
public class RunDocker extends AbstractMojo {

    @Parameter( property = "outputDir", defaultValue = "${project.basedir}",  required = true )
    private File outputDirectory;

    @Parameter( property = "run.box", defaultValue = "precise64" )
    private String box;

    @Parameter( property = "run.boxUrl", defaultValue = "http://files.vagrantup.com/precise64.box" )
    private String boxUrl;

    @Parameter( property = "run.networks", defaultValue = "" )
    private List<String> networks;

    @Parameter( property = "run.containers" )
    private List<Container> containers;

    @Parameter( property = "run.cacheConfiguration", defaultValue = "true")
    private boolean cacheConfiguration = true;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Vagrant.run(new VagrantFile(outputDirectory, box, boxUrl, networks, containers), cacheConfiguration);
        } catch (IOException e) {
            throw new MojoExecutionException("Run Failed",e);
        }
    }
}
