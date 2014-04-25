package com.imageinatelabs;


import com.imaginatelabs.dockervagrant.Vagrant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;

@Mojo( name = "stop")
public class StopDocker extends AbstractMojo {

    @Parameter( property = "outputDir", defaultValue = "${project.build.directory}",  required = true )
    private File outputDirectory;

    @Parameter( property = "arg", defaultValue = Vagrant.DESTROY)
    private String arg;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        if(StringUtils.contains(arg, Vagrant.DESTROY)){
            Vagrant.destroy(outputDirectory.toPath());
        }else if(StringUtils.contains(arg, Vagrant.HALT)){
            Vagrant.halt(outputDirectory.toPath());
        }else if(StringUtils.contains(arg, Vagrant.SUSPEND)){
            Vagrant.suspend(outputDirectory.toPath());
        }
    }
}
