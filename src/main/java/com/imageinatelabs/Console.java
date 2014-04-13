package com.imageinatelabs;


import org.apache.maven.plugin.logging.Log;

import java.io.*;

public class Console {
    public static int exec(String command, String[] envp, File dir, Log log) throws IOException, InterruptedException {
        Process pr = Runtime.getRuntime().exec(command, envp, dir);

        StreamGobbler.startOutputStreamGobbler(pr.getInputStream(), log);
        StreamGobbler.startErrorStreamGobbler(pr.getErrorStream(), log);

        int exitCode = pr.waitFor();
        if(exitCode!=0){
            log.error("Failed with Exit Code " + exitCode);
        }
        return exitCode;
    }
}

class StreamGobbler extends Thread{
    enum Type {ERROR, OUTPUT};
    InputStream is;
    Type type;
    Log log;

    StreamGobbler(InputStream is, Type type, Log log){
        this.is = is;
        this.type = type;
        this.log = log;
    }

    public static StreamGobbler startOutputStreamGobbler(InputStream is, Log log){
        StreamGobbler streamGobbler = new StreamGobbler(is, Type.OUTPUT, log);
        streamGobbler.start();
        return streamGobbler;
    }

    public static StreamGobbler startErrorStreamGobbler(InputStream is, Log log){
        StreamGobbler streamGobbler = new StreamGobbler(is, Type.ERROR, log);
        streamGobbler.start();
        return streamGobbler;
    }

    public void run(){
        try{
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while( (line = br.readLine()) != null){
                switch(type){
                    case OUTPUT: log.info(line);
                        break;
                    case ERROR: log.error(line);
                        break;
                    default: System.out.println(line);
                        break;
                }
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
