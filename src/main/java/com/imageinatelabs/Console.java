package com.imageinatelabs;


import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Console {
    public static int exec(String command, String[] envp, File dir, Log log) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command,envp, dir);
        Scanner scanner = new Scanner(pr.getInputStream());
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            if(StringUtils.isEmpty(line)){
                line = scanner.next();
            }
            log.info(line);
        }
        int exitVal = pr.waitFor();
        if(exitVal!=0){
            log.error("Failed with Exit Code " + exitVal);
        }
        return exitVal;
    }
}
