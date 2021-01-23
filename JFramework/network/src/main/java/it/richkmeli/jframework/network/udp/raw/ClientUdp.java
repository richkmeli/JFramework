package it.richkmeli.jframework.network.udp.raw;

import it.richkmeli.jframework.util.log.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientUdp {
    public static final int SOCKET_TIMEOUT = 8000;
    private static DatagramSocket clientSocket;

    public static String sendPacket(String serverAddress, int port, String packet) {
        String response = null;
        try {
            clientSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverAddress);

            byte[] buf = packet.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, address, port);
            clientSocket.send(datagramPacket);
            datagramPacket = new DatagramPacket(buf, buf.length);
            clientSocket.setSoTimeout(SOCKET_TIMEOUT);
            clientSocket.receive(datagramPacket);
            response = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        } catch (IOException e) {
            Logger.error(e);
        } finally {
            if(!clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
        return response;
    }


}

