package it.richkmeli.jframework.util.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Logger {
    private static File logFile;
    private static final String DEFAULT_LOG_FILENAME = "log.txt";
    // set as false for release, in which is not need to log information.
    public static Boolean debug = true;
    public static final String INFO_TAG = "INFO";
    public static final String ERROR_TAG = "ERROR";
    public static final String WARNING_TAG = "WARNING";

    // Logger.info(this.getClass(), "...");
    public static void info(String message) {
        if (debug) {
            System.out.println(formatLogEvent(INFO_TAG, message));
            printOnLogFile(INFO_TAG, message);
        }
    }

    public static void warning(String message) {
        if (debug) {
            System.out.println(formatLogEvent(WARNING_TAG, message));
            printOnLogFile(WARNING_TAG, message);
        }
    }

    public static void error(String message) {
        if (debug) {
            System.err.println(formatLogEvent(ERROR_TAG, message));
            printOnLogFile(ERROR_TAG, message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (debug) {
            System.err.println(formatLogEvent(ERROR_TAG, message + " || " + throwable.getMessage()));
            printOnLogFile(ERROR_TAG, message + " || " + throwable.getMessage());
        }
    }

    public static void error(Throwable throwable) {
        if (debug) {
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

    private static File getLogFile() {
        if (logFile == null) {
            //System.out.println(prefixSystemOut + "instantiating logFile");

            String logFilename = null;
            // get filename from resource
            ResourceBundle resource = ResourceBundle.getBundle("configuration");
            try {
                logFilename = resource.getString("logger.filename");
            } catch (MissingResourceException missingResourceException) {
                // if the exception is threw, it is kept the default file name.
                missingResourceException.printStackTrace();
                System.err.println(formatLogEvent(ERROR_TAG, "filename in resource not found"));
            }
            if (logFilename != null) {
                logFile = createAndVerifyPermissions(logFilename);
            }

            // Using default filename for logfile
            if (logFile == null) {
                logFile = createAndVerifyPermissions(DEFAULT_LOG_FILENAME);
            }

        }
        return logFile;
    }

    private static File createAndVerifyPermissions(String logFilename) {
        File tmpFile = new File(logFilename);
        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            System.err.println(formatLogEvent(ERROR_TAG, "filename in resource: '" + logFilename + "' doesn't have creation permission. Access is denied."));
            return null;
        }
        // if the file doesn't have permissions, it is kept the default file name.
        if (tmpFile.canWrite()) {
            //logFile = tmpFile;
        } else {
            //System.err.println(prefixSystemOut + "filename in resource doesn't have write permission");
            System.err.println(formatLogEvent(ERROR_TAG, "filename in resource: '" + logFilename + "' doesn't have write permission"));
            return null;
        }
        return tmpFile;
    }

    private static void printOnLogFile(String messageType, String s) {
        File logfile = getLogFile();
        if (logfile != null) {
            FileWriter fr = null;
            try {
                fr = new FileWriter(logfile, true);
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
        } else {
            System.err.println(formatLogEvent(ERROR_TAG, "Unable to create log file. Set a correct one in resource (logger.filename=PATH)"));
        }
    }


    public static void checkLogFileDimension() {
        if (getFileSizeMegaBytes(getLogFile()) > 50) {
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
