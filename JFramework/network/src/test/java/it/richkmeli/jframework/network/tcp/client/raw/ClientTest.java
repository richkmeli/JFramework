package it.richkmeli.jframework.network.tcp.client.raw;

import it.richkmeli.jframework.network.util.CommunicationLock;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class ClientTest {

    @Test
    public void sendPacket() {
        String response = Client.sendPacket("www.google.com", 80, "TEST");

        System.out.println("response length: "+response.length());
        assert !"".equalsIgnoreCase(response);
    }

    @Test
    public void doTask() {
        Client.doTask("www.google.com", 80, new ClientTask() {
            @Override
            public void doStuff(Socket clientSocket, CommunicationLock communicationLock) throws IOException {
                Client.send(clientSocket,"TEST");
                String response = communicationLock.getMessage();
                System.out.println("response length: "+response.length());
                assert !"".equalsIgnoreCase(response);
            }
        });
    }
}