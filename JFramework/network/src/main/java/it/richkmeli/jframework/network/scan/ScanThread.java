package it.richkmeli.jframework.network.scan;

import it.richkmeli.jframework.util.log.Logger;

import java.io.IOException;
import java.net.InetAddress;

public class ScanThread extends Thread{
    private static final int timeout = 2000;
    private String host;
    private String hostname;
    private String hostAddress;


    public ScanThread(String host){
        this.host = host;
    }

    @Override
    public void run() {
        super.run();
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            if (inetAddress.isReachable(timeout)) {
                this.hostname = inetAddress.getCanonicalHostName();
                this.hostAddress = inetAddress.getHostAddress();
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public String getHostname() {
        return hostname;
    }

    public String getHostAddress() {
        return hostAddress;
    }
}
