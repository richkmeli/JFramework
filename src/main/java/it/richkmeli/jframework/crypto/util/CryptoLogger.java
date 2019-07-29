/*package it.richkmeli.jframework.crypto.util;

public class Logger {
    public static final Boolean DEBUG = true;

    public static void info(String message) {
        if (DEBUG) {
            System.out.println(getTag() + " - " + message);
        }
    }

    public static void error(String message) {
        if (DEBUG) {
            System.err.println(getTag() + " - " + message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (DEBUG) {
            System.err.println(getTag() + " - " + message + " || " + throwable.getMessage());
        }
    }

    private static String getTag() {
        String className = new Exception().getStackTrace()[2].getClassName();
        return "CryptoLOG::" + className.substring(1 + className.lastIndexOf('.'));
    }
}*/
