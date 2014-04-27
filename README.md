#WARNING
This plugin is still very much in alpha and is casually being developed as I need features to support another project I'm working on. The plugin is unstable and should be used at your own risk or if you know what you're doing with vagrant, docker, maven and java. If you like what I've started and want to contribute to this project feel free to get in contact with me or send me a pull request!

# Docker Maven Plugin
The the docker-maven-plugin provides the ability to run containers in the maven build life cycle

## Requirements
Because docker-maven-plugin is just a really thin wrapper around Vagrant and Docker you will require the following software to make this work.

* VirtualBox => v4.3.8 (Currently this plugin has only been tested against VirtualBox)
* Vagrant    => v1.5.1

## Configuration
There are two components Vagrant and Docker that you need to configure in your in the plugins section of your pom.xml.
This configuration maps closely to the Vagrant docker provider configuration found at http://docs.vagrantup.com/v2/provisioning/docker.html

```xml
<properties>
  <mongoDb.password>mypass</mongoDb.password>
  <mongoDb.port>27017</mongoDb.port>
  <mysql.password>mypass</mysql.password>
  <mysql.port>3306</mysql.port>
</properties>
<build>
  <plugins>
    <plugin>
      <groupId>com.imaginatelabs</groupId>
      <artifactId>docker-maven-plugin</artifactId>
      <version>1.0-SNAPSHOT</version>
      <configuration>

        <!-- Directory where the Vagrantfile will be run and executed from -->
        <outputDirectory></outputDirectory>

        <!-- Vagrant Box you wish to run the Docker containers on. -->
        <box></box>

        <!-- Url to the box if it's not already in the Vagrant cache. -->
        <boxUrl></boxUrl>

        <!-- Network configuration for Vagrant e.g. port forwarding -->
        <networks>
          <network>"forwarded_port", guest: ${mongoDb.port}, host: ${mongoDb.port}</network>
          <network>"forwarded_port", guest: ${mysql.port}, host: ${mysql.port}</network>
        </networks>

        <!-- Docker Container Specification -->
        <containers>
          <container>
            <name>MongoDb</name>
            <image>tutum/mongodb</image>
            <args>-p ${mongoDb.port}:${mongoDb.port} -e MONGODB_PASS='${mongoDb.password}'</args>
            <cmd>mongod --fork --logpath /var/log/mongodb.log</cmd>
            <!-- docker run -d -p 27017:27017 -e MONGODB_PASS="mypass" -i -t tutum/mongodb /bin/bash -->
          </container>
          <container>
            <name>MySql</name>
            <image>tutum/mysql</image>
            <args>-p ${mysql.port}:${mysql.port} -e MYSQL_PASS='${mysql.password}'</args>
            <cmd></cmd>
          </container>
        </containers>

        <!-- Docker Container Specification -->
        <postShellScript>relative/path/from/vagrantfile/directory/script.sh</postShellScript>
    </configuration>
    </plugin>
  <plugins>
</build>
```
### Explanation
The above configuration create a VagrantBox that has a MySql container and MongoDb container with both ports forwarded
as well as passwords set by a property specified in the maven properties section of the pom.xml file.

## Commands
### Run
Runs a Vagrant box with any specified Docker Containers or shell scripts
```
> mvn docker-maven-plugin:run
```

### Stop
Destroys a running Vagrant box
 ```
 > mvn docker-maven-plugin:stop
 ```
