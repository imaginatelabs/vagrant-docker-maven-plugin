package com.imaginatelabs.dockervagrant.tests;

import com.imaginatelabs.dockervagrant.Container;
import com.imaginatelabs.dockervagrant.Vagrant;
import com.imaginatelabs.dockervagrant.VagrantFile;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VagrantFileTests {

    @Test
    public void shouldCreateVagrantFileWithMandatoryValues() throws IOException {
        Path tempDirectory = Files.createTempDirectory("vagrantFileTests");
        List<String> networks = new ArrayList<String>();
        List<Container> containers = new ArrayList<Container>();

        VagrantFile vagrantFile = new VagrantFile(tempDirectory.toFile(),"mybox.box","https://my.url",networks,containers);

        String actualVagrantFileAsText = vagrantFile.toText();
        String expectedVagrantFileAsText =
                "# -*- mode: ruby -*-\n" +
                "# vi: set ft=ruby :\n" +
                "\n" +
                "Vagrant.configure(\"2\") do |config|\n" +
                "\tconfig.vm.box = \"mybox.box\"\n" +
                "\tconfig.vm.box_url = \"https://my.url\"\n" +
                "end";

        Assert.assertEquals(actualVagrantFileAsText, expectedVagrantFileAsText);
    }

    @Test
    public void shouldCreateVagrantFileWithAnAdditionalSingleNetworkValues() throws IOException {
        Path tempDirectory = Files.createTempDirectory("vagrantFileTests");
        List<String> networks = new ArrayList<String>();
        networks.add("\"forwarded_port\", guest: 80, host: 80");
        List<Container> containers = new ArrayList<Container>();

        VagrantFile vagrantFile = new VagrantFile(tempDirectory.toFile(),"mybox.box","https://my.url",networks,containers);

        String actualVagrantFileAsText = vagrantFile.toText();
        String expectedVagrantFileAsText =
                "# -*- mode: ruby -*-\n" +
                "# vi: set ft=ruby :\n" +
                "\n" +
                "Vagrant.configure(\"2\") do |config|\n" +
                "\tconfig.vm.box = \"mybox.box\"\n" +
                "\tconfig.vm.box_url = \"https://my.url\"\n" +
                "\tconfig.vm.network  \"forwarded_port\", guest: 80, host: 80\n" +
                "end";

        Assert.assertEquals(actualVagrantFileAsText, expectedVagrantFileAsText);
    }

    @Test
    public void shouldCreateVagrantFileWithAdditionalMultipleNetworksValues() throws IOException {
        Path tempDirectory = Files.createTempDirectory("vagrantFileTests");
        List<String> networks = new ArrayList<String>();
        networks.add("\"forwarded_port\", guest: 80, host: 80");
        networks.add("\"forwarded_port\", guest: 443, host: 443");
        List<Container> containers = new ArrayList<Container>();

        VagrantFile vagrantFile = new VagrantFile(tempDirectory.toFile(),"mybox.box","https://my.url",networks,containers);

        String actualVagrantFileAsText = vagrantFile.toText();
        String expectedVagrantFileAsText =
                "# -*- mode: ruby -*-\n" +
                        "# vi: set ft=ruby :\n" +
                        "\n" +
                        "Vagrant.configure(\"2\") do |config|\n" +
                        "\tconfig.vm.box = \"mybox.box\"\n" +
                        "\tconfig.vm.box_url = \"https://my.url\"\n" +
                        "\tconfig.vm.network  \"forwarded_port\", guest: 80, host: 80\n" +
                        "\tconfig.vm.network  \"forwarded_port\", guest: 443, host: 443\n" +
                        "end";

        Assert.assertEquals(actualVagrantFileAsText, expectedVagrantFileAsText);
    }

    @Test
    public void shouldCreateVagrantFileWithAdditionalSingleDockerContainerValues() throws IOException {
        Path tempDirectory = Files.createTempDirectory("vagrantFileTests");
        List<String> networks = new ArrayList<String>();
        List<Container> containers = new ArrayList<Container>();
        containers.add(new Container("myDockerName","myDockerImage","myArgs","myCmds"));

        VagrantFile vagrantFile = new VagrantFile(tempDirectory.toFile(),"mybox.box","https://my.url",networks,containers);

        String actualVagrantFileAsText = vagrantFile.toText();
        String expectedVagrantFileAsText =
                "# -*- mode: ruby -*-\n" +
                "# vi: set ft=ruby :\n" +
                "\n" +
                "Vagrant.configure(\"2\") do |config|\n" +
                    "\tconfig.vm.box = \"mybox.box\"\n" +
                    "\tconfig.vm.box_url = \"https://my.url\"\n" +
                    "\tconfig.vm.provision \"docker\" do |d|\n" +
                        "\t\td.run \"myDockerName\", image:\"myDockerImage\" ,\n" +
                            "\t\t\targs: \"myArgs\" ,\n" +
                            "\t\t\tcmd: \"myCmds\"\n" +
                        "\tend\n" +
                "end";

        Assert.assertEquals(actualVagrantFileAsText, expectedVagrantFileAsText);
    }

    @Test
    public void shouldCreateVagrantFileWithAdditionalMultipleDockerContainerValues() throws IOException {
        Path tempDirectory = Files.createTempDirectory("vagrantFileTests");
        List<String> networks = new ArrayList<String>();
        List<Container> containers = new ArrayList<Container>();
        containers.add(new Container("myDockerName1","myDockerImage1","myArgs1","myCmds1"));
        containers.add(new Container("myDockerName2","myDockerImage2","myArgs2","myCmds2"));

        VagrantFile vagrantFile = new VagrantFile(tempDirectory.toFile(),"mybox.box","https://my.url",networks,containers);

        String actualVagrantFileAsText = vagrantFile.toText();
        String expectedVagrantFileAsText =
                "# -*- mode: ruby -*-\n" +
                "# vi: set ft=ruby :\n" +
                "\n" +
                "Vagrant.configure(\"2\") do |config|\n" +
                    "\tconfig.vm.box = \"mybox.box\"\n" +
                    "\tconfig.vm.box_url = \"https://my.url\"\n" +
                    "\tconfig.vm.provision \"docker\" do |d|\n" +
                        "\t\td.run \"myDockerName1\", image:\"myDockerImage1\" ,\n" +
                            "\t\t\targs: \"myArgs1\" ,\n" +
                            "\t\t\tcmd: \"myCmds1\"\n" +
                        "\t\td.run \"myDockerName2\", image:\"myDockerImage2\" ,\n" +
                            "\t\t\targs: \"myArgs2\" ,\n" +
                            "\t\t\tcmd: \"myCmds2\"\n" +
                    "\tend\n" +
                "end";

        Assert.assertEquals(actualVagrantFileAsText, expectedVagrantFileAsText);
    }
}
