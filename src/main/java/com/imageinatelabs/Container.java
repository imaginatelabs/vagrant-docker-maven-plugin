package com.imageinatelabs;

import org.codehaus.plexus.util.StringUtils;

public class Container {

    private String name;
    private String image;
    private String args;
    private String cmd;

    public Container() { }

    public Container(String name, String image, String args, String cmd) {
        this.name = name;
        this.image = image;
        this.args = args;
        this.cmd = cmd;
    }

    public String printPull(){
        return String.format("\t\td.pull_images \"%s\"\n", image);
    }

    public String printRun(){
        return String.format("\t\td.run \"%s\", image:\"%s\" %s %s\n",
            name,
            image,
            println("args",args),
            println("cmd",cmd));
    }

    private String println(String key, String value){
        return StringUtils.isNotEmpty(value) ? String.format(",\n\t\t\t%s: \"%s\"", key, value) : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
