package it.richkmeli.jframework.network.tcp.client.raw;

import it.richkmeli.jframework.network.util.CommunicationLock;

import java.io.IOException;
import java.net.Socket;

public interface ClientTask {
    void doStuff(Socket clientSocket, CommunicationLock communicationLock) throws IOException;
}
