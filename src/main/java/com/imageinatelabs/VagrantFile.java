package com.imageinatelabs;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VagrantFile {
    public static final String FILE_NAME = "Vagrantfile";
    private final List<Container> containers;
    private String box;
    private String boxUrl;
    private String network;
    private File path;

    public VagrantFile(File path, String box, String boxUrl, String network, List<Container> containers) {
        this.box = box;
        this.boxUrl = boxUrl;
        this.network = network;
        this.containers = containers;
        this.path = path;
    }

    public String toFileFormat(){
        return String.format("%s%s%s%s%s%s",
                "# -*- mode: ruby -*-\n# vi: set ft=ruby :\n\nVagrant.configure(\"2\") do |config|\n",
                println("config.vm.box",box),
                println("config.vm.box_url", boxUrl),
                println("config.vm.network", "", network, ""),
                printDocker(containers),
                "end");
    }

    private String println(String key, String delimiter, String value, String quote){
        String prefix = StringUtils.isNotEmpty(value) ? "\t" : "#\t";
        return String.format("%s%s %s %s%s%s\n", prefix, key, delimiter, quote, value, quote);
    }

    private String println(String key, String value){
        return println(key, "=", value, "\"");
    }

    private String printDocker(List<Container> containers){
        if(containers.isEmpty()){
            return "";
        }
        String str =  "\tconfig.vm.provision \"docker\" do |d|\n";

        for(Container container : containers){
            str += container.printPull();
        }

        for(Container container : containers){
            str += container.printRun();
        }

        str += "\tend\n";
        return str;
    }

    public Path getPath() {
        return path.toPath();
    }

    public Path getFullPath() {
        return new File(path, FILE_NAME).toPath();
    }

    public byte[] getBytes() {
        return toFileFormat().getBytes();
    }
}
