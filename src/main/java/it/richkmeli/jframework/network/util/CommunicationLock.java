package it.richkmeli.jframework.network.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class CommunicationLock {
    private volatile AtomicBoolean isFinished = new AtomicBoolean(true);
    private volatile String message;

    public String getMessage() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = message;
        message = "";
        return s;
    }

    public void append(char character) {
        message += (char) character;
    }

    public void append(String string) {
        message += string;
    }

    public boolean isFinished() {
        return this.isFinished.get();
    }

    public void setFinished(boolean isFinished) {
        this.isFinished.set(isFinished);
    }
}
