package it.richkmeli.jframework.network.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class CommunicationLock {
    private static volatile AtomicBoolean isFinished = new AtomicBoolean(true);
    private static volatile String message;

    public static String getMessage() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = CommunicationLock.message;
        CommunicationLock.message = "";
        return s;
    }

    public static void append(char character) {
        message += (char) character;
    }

    public static boolean isFinished() {
        return isFinished.get();
    }

    public static void setFinished(boolean isFinished) {
        CommunicationLock.isFinished.set(isFinished);
    }
}
