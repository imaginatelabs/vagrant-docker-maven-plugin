package com.imageinatelabs;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;


public class LogF {

    private static class SingletonLogF {
        static final LogF INSTANCE = new LogF();
    }

    private Log log = new SystemStreamLog();

    public Log getLog() {
        return log;
    }

    public static void i(String format, Object... args){
        SingletonLogF.INSTANCE.getLog().info(String.format(format, args));
    }

    public static void d(String format, Object... args){
        SingletonLogF.INSTANCE.getLog().debug(String.format(format, args));
    }

    public static void e(String format, Object... args){
        SingletonLogF.INSTANCE.getLog().error(String.format(format, args));
    }

    public static void w(String format, Object... args){
        SingletonLogF.INSTANCE.getLog().warn(String.format(format, args));
    }

    public static Log l(){
        return SingletonLogF.INSTANCE.getLog();
    }
}
