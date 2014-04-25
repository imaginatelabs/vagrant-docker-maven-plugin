package com.imageinatelabs;


import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.util.List;

public class Console {
    public static int exec(String command, String[] envp, File dir) throws IOException, InterruptedException {
        Process pr = Runtime.getRuntime().exec(command, envp, dir);

        StreamGobbler.startOutputStreamGobbler(pr.getInputStream());
        StreamGobbler.startErrorStreamGobbler(pr.getErrorStream());

        int exitCode = pr.waitFor();
        if(exitCode!=0){
            LogF.e("Failed with Exit Code " + exitCode);
        }
        return exitCode;
    }

    public static int exec(String command, String[] envp, File dir, List<String> output) throws IOException, InterruptedException {
        Process pr = Runtime.getRuntime().exec(command, envp, dir);

        StreamGobbler.startOutputStreamGobbler(pr.getInputStream(), output);
        StreamGobbler.startErrorStreamGobbler(pr.getErrorStream(), output);

        int exitCode = pr.waitFor();
        if(exitCode!=0){
            output.add("Failed with Exit Code " + exitCode);
        }
        return exitCode;
    }
}

class StreamGobbler extends Thread{
    enum Type {ERROR, OUTPUT};
    InputStream is;
    Type type;
    List<String> output;

    StreamGobbler(InputStream is, Type type){
        this.is = is;
        this.type = type;
    }

    StreamGobbler(InputStream is, Type type, List<String> output){
        this.is = is;
        this.type = type;
        this.output = output;
    }

    public static StreamGobbler startOutputStreamGobbler(InputStream is){
        StreamGobbler streamGobbler = new StreamGobbler(is, Type.OUTPUT);
        streamGobbler.start();
        return streamGobbler;
    }

    public static StreamGobbler startErrorStreamGobbler(InputStream is){
        StreamGobbler streamGobbler = new StreamGobbler(is, Type.ERROR);
        streamGobbler.start();
        return streamGobbler;
    }

    public static StreamGobbler startOutputStreamGobbler(InputStream is, List<String> output){
        StreamGobbler streamGobbler = new StreamGobbler(is, Type.OUTPUT, output);
        streamGobbler.start();
        return streamGobbler;
    }

    public static StreamGobbler startErrorStreamGobbler(InputStream is, List<String> output){
        StreamGobbler streamGobbler = new StreamGobbler(is, Type.ERROR, output);
        streamGobbler.start();
        return streamGobbler;
    }

    public void writeLine(String line){
        if(output != null){
            output.add(line);
        }else{
            LogF.l().info(line);
        }
    }
    public void writeError(String line){
        if(output != null){
            output.add(line);
        }else{
            LogF.l().error(line);
        }
    }

    public void run(){
        try{
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while( (line = br.readLine()) != null){
                switch(type){
                    case OUTPUT: writeLine(line);
                        break;
                    case ERROR: writeError(line);
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
