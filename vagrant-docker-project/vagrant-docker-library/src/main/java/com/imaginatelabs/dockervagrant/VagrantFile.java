package com.imaginatelabs.dockervagrant;

import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class VagrantFile {
    public static final String FILE_NAME = "Vagrantfile";
    public static final String HASH_FILE_NAME = "Vagrantfile.hash";
    private final List<Container> containers;
    private String box;
    private String boxUrl;
    private List<String> networks;
    private File path;
    private String configCacheHash;
    private String postShellScript;

    public VagrantFile(File path, String box, String boxUrl, List<String> networks, List<Container> containers) {
        this.box = box;
        this.boxUrl = boxUrl;
        this.networks = networks;
        this.containers = containers;
        this.path = path;
    }

    public VagrantFile(File path, String box, String boxUrl, List<String> networks, List<Container> containers, String postShellScript) {
        this.box = box;
        this.boxUrl = boxUrl;
        this.networks = networks;
        this.containers = containers;
        this.path = path;
        this.postShellScript = postShellScript;
    }

    public String toText(){
        return String.format("%s%s%s%s%s%s%s",
                "# -*- mode: ruby -*-\n# vi: set ft=ruby :\n\nVagrant.configure(\"2\") do |config|\n",
                printBox(),
                println("config.vm.box_url", boxUrl, hasConfigCache()),
                printNetworks(),
                printDocker(),
                printPostShellScript(),
                "end");
    }

    private String printPostShellScript() {
        return StringUtils.isNotEmpty(postShellScript) ? println("config.vm.provision \"shell\",","path:", postShellScript,"\""): "";
    }

    private String printBox(){
        return hasConfigCache() ? println("config.vm.box", configCacheHash) : println("config.vm.box",box);
    }

    private boolean hasConfigCache() {
        return StringUtils.isNotBlank(configCacheHash);
    }

    private String printNetworks(){
        String results = "";
        for(String network : networks) {
            results += println("config.vm.network", "", network, "");
        }
        return results;
    }

    private String println(String key, String delimiter, String value, String quote){
        String prefix = StringUtils.isNotEmpty(value) ? "\t" : "#\t";
        return String.format("%s%s %s %s%s%s\n", prefix, key, delimiter, quote, value, quote);
    }

    private String println(String key, String delimiter, boolean configCache){
        if(!configCache){
            return println(key,delimiter);
        }
        return "";
    }

    private String println(String key, String value){
        return println(key, "=", value, "\"");
    }

    private String printDocker(){
        if(containers.isEmpty()){
            return "";
        }
        String str =  "\tconfig.vm.provision \"docker\" do |d|\n";

        if(!hasConfigCache()) {
            for (Container container : containers) {
                str += container.printPull();
            }
        }

        for(Container container : containers){
            str += container.printRun();
        }

        str += "\tend\n";
        return str;
    }

    public String getHexHashCode(){
        return Integer.toHexString(this.toText().hashCode());
    }

    public byte[] getHexHashCodeAsBytes(){
        return Integer.toHexString(this.toText().hashCode()).getBytes();
    }

    public Path getPath() {
        return path.toPath();
    }

    public Path getFullPath() {
        return new File(path, FILE_NAME).toPath();
    }

    public byte[] getBytes() {
        return toText().getBytes();
    }

    public Path getFullHashFilePath() {
        return new File(path,HASH_FILE_NAME).toPath();
    }

    public void useCachedBox(String configCacheHash) {
        this.configCacheHash = configCacheHash;
    }
}
