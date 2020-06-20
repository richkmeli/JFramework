package it.richkmeli.jframework.network.scan;

import it.richkmeli.jframework.util.log.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkScanner {

    public static List<String> getActiveHosts(String subnet) {
        List<String> activehosts = new ArrayList<>();

        List<ScanThread> scanThreads = new ArrayList<>();
        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            scanThreads.add(new ScanThread(host));
        }

        for (ScanThread scanThread : scanThreads) {
            scanThread.start();
        }

        boolean allThreadsDied;
        do {
            allThreadsDied = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (ScanThread scanThread : scanThreads) {
                if (scanThread.isAlive()) {
                    allThreadsDied = true;
                    break;
                }
            }
        } while (allThreadsDied);

        for (ScanThread scanThread : scanThreads) {
            String hostname = scanThread.getHostname();
            String hostAddress = scanThread.getHostAddress();
            if (hostname != null) {
                activehosts.add(hostname + " (" + hostAddress + ")");
                //Logger.info(hostname + " (" + hostAddress + ")");
            }
        }

        return activehosts;
    }

    public static List<InetAddress> getActiveHostsSync(String subnet) {
        List<InetAddress> activehosts = new ArrayList<>();
        int timeout = 1000;

        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                if (inetAddress.isReachable(timeout)) {
                    activehosts.add(inetAddress);

                    //System.out.println(inetAddress.getCanonicalHostName() + " - " + inetAddress.getHostAddress());
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        return activehosts;
    }

}
