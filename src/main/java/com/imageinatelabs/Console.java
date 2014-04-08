package com.imageinatelabs;


import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {
    public static int exec(String command, String[] envp, File dir, Log log) throws IOException, InterruptedException {
        //TODO Manage dynamic input e.g. Loading that updates the line
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command,envp, dir);
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        String line=null;
        while((line=input.readLine()) != null) {
            log.info(line);
        }
        while((line=error.readLine()) != null) {
            log.error(line);
        }

        int exitVal = pr.waitFor();
        if(exitVal!=0){
            log.error("Failed with Exit Code " + exitVal);
        }
        return exitVal;
    }
}
