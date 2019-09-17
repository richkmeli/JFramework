package it.richkmeli.jframework.network.udp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class P2pChatARQ {
    // TODO test dim massima e minima
    private static final int packetSize = 10; // packet size. Text is divided by packet size.
    private ListenThread listenThread;

    public void startListen(Integer listenPort) {
        startListen(listenPort, false);
    }

    public void startListen(Integer listenPort, boolean verboseMode) {
        listenThread = new ListenThread(listenPort, packetSize, verboseMode);
        listenThread.start();
    }

    public void startTalk(String receiverIP, Integer receiverPort) {
        startTalk(receiverIP, receiverPort, null, null, false);
    }

    public void startTalk(String receiverIP, Integer receiverPort, Integer outPort, InputStream inputStream, boolean verboseMode) {
        // send
        if (inputStream == null) {
            // interactive
            inputStream = System.in;
            TalkRunnable talkRunnable = new TalkRunnable(packetSize, outPort, listenThread, receiverIP, receiverPort, inputStream, verboseMode);
            talkRunnable.run();
        } else {
            // thread
            Thread thread = new Thread(new TalkRunnable(packetSize, outPort, listenThread, receiverIP, receiverPort, inputStream, verboseMode));
            thread.start();
        }
    }


    public static void sendPacket(DatagramSocket socket, String receiverIP, int receiverPort, int numPacket, String bufferFile, PrintStream printStream, boolean verboseMode) throws IOException {
        String composedString = String.format("%04d", numPacket) + bufferFile;    // stringa formata da intestazione e dati
        byte[] buffer = composedString.getBytes();

        InetAddress addr = InetAddress.getByName(receiverIP);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, receiverPort);
        socket.send(packet);    // invio pacchetto
        if (verboseMode) printStream.print("\t\tSend : " + numPacket);

        // INFORMAZIONI DELL'ACK MANDATO DAL SERVER
        String ackPacket;
        int ackNum = 0;
        do {
            try {
                socket.setSoTimeout(2000);    // TEMPORIZZAZIONE: lancia eccezione(SocketTimeoutException) se non si riceve nulla in 2 secondi
                socket.receive(packet); // se non riceviamo riscontro(ACK) dal server continuamo ad inviare
                ackPacket = new String(packet.getData(), 0, packet.getLength()); //(byte[] byteArray, int offset, int count), count= dimensione pacchetto ricevuto
                ackNum = Integer.parseInt(ackPacket.substring(3, ackPacket.length()));  // ack della forma: ACK0002 , cioe "ACK" + numPacket
                if (verboseMode) printStream.print("\tReceived: " + ackPacket);
            } catch (SocketTimeoutException e) {
                socket.send(packet);    // quando viene lanciata eccezione rinvio pacchetto
                if (verboseMode) printStream.print("\tOver timeout...\nresend : " + numPacket);
            }
        } while (ackNum != numPacket);
    }

    public String getMessage() {
        return listenThread.getMessage();
    }

}