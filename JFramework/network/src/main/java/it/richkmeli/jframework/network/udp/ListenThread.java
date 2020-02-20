package it.richkmeli.jframework.network.udp;

import it.richkmeli.jframework.network.util.CommunicationLock;
import it.richkmeli.jframework.util.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ListenThread extends Thread {
    private Integer listenPort;
    private Integer packetSize;
    private boolean verboseMode;
    private CommunicationLock communicationLock;
    private boolean isLast = false;
    private PrintStream printStream;

    public PrintStream getPrintStream() {
        return printStream;
    }


    public ListenThread(Integer listenPort, Integer packetSize, boolean verboseMode) {
        this.listenPort = listenPort;
        this.packetSize = packetSize;
        this.verboseMode = verboseMode;
        this.printStream = System.out;
        communicationLock = new CommunicationLock();
    }

    public ListenThread(Integer listenPort, Integer packetSize, PrintStream printStream, boolean verboseMode) {
        this.listenPort = listenPort;
        this.packetSize = packetSize;
        this.verboseMode = verboseMode;
        this.printStream = printStream;
        communicationLock = new CommunicationLock();
    }

    @Override
    public synchronized void start() {
        super.start();
        if (verboseMode) Logger.info("ListenThread has started...");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (verboseMode) System.out.println("Thread Listening Stopped...");
    }

    @Override
    public void run() {
        try {


            // CONNESSIONE LISTEN
            DatagramSocket listenSocket = new DatagramSocket(listenPort);    // apertura socket sulla porta

            byte[] listenBuffer = new byte[packetSize + 4];    // NumeroPacchetto(4)+ContenutoPacchetto(packetSize), i 4 in piu sono per la gestione pacchetti
            DatagramPacket listenPacket = new DatagramPacket(listenBuffer, listenBuffer.length);    // pacchetto a datagramma
            int listenNumPacket = 0;    // per gestione numero pacchetti, e per ACK di ritorno

            while (!isLast) {
                listenSocket.receive(listenPacket);

                String packetContent = new String(listenPacket.getData(), 0, listenPacket.getLength()); //(byte[] byteArray, int offset, int count), count= dimensione pacchetto ricevuto
                listenNumPacket = Integer.parseInt(packetContent.substring(0, 4)); // primi 4 caratteri utilizzati per la numerazione
                // GESTIONE PACCHETTI IN ARRIVO
                String infoPacket = packetContent.substring(4/*, packetContent.length()*/);
                printStream.print(infoPacket);
                communicationLock.append(infoPacket);

                if (verboseMode)
                    printStream.println("\t\t Riceived from: " + listenPacket.getAddress() + " : " + listenPacket.getPort());
                // INVIO ACK DI AVVENUTA RICEZIONE
                listenBuffer = ("ACK" + String.format("%04d", listenNumPacket)).getBytes();    // ACK per secondo pacchetto con formato: ACK0002
                DatagramPacket packetACK = new DatagramPacket(listenBuffer, listenBuffer.length, listenPacket.getAddress(), listenPacket.getPort());    // mandi l'ACK sulla porta che ti ha mandato il dato
                listenSocket.send(packetACK);
            }
            listenSocket.close(); // chiusura del socket

        } catch (SocketException e) {
            Logger.error("Apertura del socket di ascolto sulla porta di ascolto " + listenPort + " fallita");
        } catch (IOException e) {
            Logger.error(e);
            //e.printStackTrace();
        }
    }

    public String getMessage() {
        return communicationLock.getMessage();
    }
}



