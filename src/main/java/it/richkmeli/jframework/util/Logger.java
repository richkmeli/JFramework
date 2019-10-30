package it.richkmeli.jframework.util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Logger {
    private static final String LOG_FILENAME = "log.txt";
    public static final Boolean DEBUG = true;
    public static final String INFO_TAG = "INFO";
    public static final String ERROR_TAG = "ERROR";

    // Logger.info(this.getClass(), "...");
    public static void info(String message) {
        if (DEBUG) {
            System.out.println(formatLogEvent(INFO_TAG, message));
            printOnLogFile(INFO_TAG, message);
        }
    }

    public static void error(String message) {
        if (DEBUG) {
            System.err.println(formatLogEvent(ERROR_TAG, message));
            printOnLogFile(ERROR_TAG, message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (DEBUG) {
            System.err.println(formatLogEvent(ERROR_TAG, message + " || " + throwable.getMessage()));
            printOnLogFile(ERROR_TAG, message + " || " + throwable.getMessage());
        }
    }

    public static void error(Throwable throwable) {
        if (DEBUG) {
            System.err.println(formatLogEvent(ERROR_TAG, throwable.getMessage()));
            printOnLogFile(ERROR_TAG, throwable.getMessage());
        }
    }


    private static String getTag(int i) {
        // get the class that calls this class. "i" is how many methods are before the call that was calling 0(getTag LOGGER), 1(info LOGGER), 2(method CLASS)
        String className = new Exception().getStackTrace()[i].getClassName();
        return /*"JFrameworkLOG::" +*/className.substring(1 + className.lastIndexOf('.'));
    }

    // default for info and error
    private static String formatLogEvent(String messageType, String s) {
        return formatLogEvent(messageType, s, 4);
    }

    private static String formatLogEvent(String messageType, String s, int i) {
        return new Timestamp(System.currentTimeMillis()) + ", " + messageType + " " + getTag(i) + " : " + s;
    }

    private static void printOnLogFile(String messageType, String s) {
        File logFile = new File(LOG_FILENAME);
        FileWriter fr = null;
        try {
            fr = new FileWriter(logFile, true);
            /*if (getTag(3) == null) {
                fr.write(new Timestamp(System.currentTimeMillis()) + " : " + s + '\n');
            } else {
                fr.write(new Timestamp(System.currentTimeMillis()) + ", " + getTag(3) + " : " + s + '\n');
            }*/
            fr.write(formatLogEvent(messageType, s, 4) + '\n');
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void checkLogFileDimension() {
        File logFile = new File(LOG_FILENAME);
        if (getFileSizeMegaBytes(logFile) > 50) {
            if (!logFile.delete()) {
                error("Logfile not found for deleting process.");
            }
        }
    }

    private static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }

    private static double getFileSizeKBytes(File file) {
        return (double) file.length() / (1024);
    }
}
