package it.richkmeli.jframework.network.udp;

import it.richkmeli.jframework.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TalkRunnable implements Runnable {
    private int packetSize;
    private Integer outPort;
    private ListenThread listenThread;
    private String receiverIP;
    private Integer receiverPort;
    private boolean verboseMode;
    private InputStream inputStream;
    public static boolean end = false;

    // communication info


    public TalkRunnable(int packetSize, Integer outPort, ListenThread listenThread, String receiverIP, Integer receiverPort, InputStream inputStream, boolean verboseMode) {
        this.packetSize = packetSize;
        this.outPort = outPort;
        this.listenThread = listenThread;
        this.receiverIP = receiverIP;
        this.receiverPort = receiverPort;
        this.verboseMode = verboseMode;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        BufferedReader inputReader;
        StringBuilder bufferFile = new StringBuilder();

        // connection
        DatagramSocket talkSocket = null; //(tuo client) socket per mandare(receiverPort) pacchetti al destinatario e ricevere(talkPacket) l'ACK
        //byte[] talkBuffer = new byte[packetSize + 4];    // NumeroPacchetto(4)+ContenutoPacchetto(packetSize), i 4 in piu sono per la gestione pacchetti
        int talkNumPacket = 0;    // per gestione numero pacchetti, e per ACK di ritorno

        // invio messagg in modo interattivo
        try {
            inputReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                if (outPort == null) {
                    talkSocket = new DatagramSocket(); // random port
                } else {
                    talkSocket = new DatagramSocket(outPort);
                }

                // TODO controlla se gia avviato da fuori
                //listenThread.start();

                int flushTimer = 0;
                int valorizedChar = 0;
                while (!end) {
                    int read = inputReader.read();
                    char charRead = (char) read;
                    if (read != -1 || (flushTimer++ >= 10 && valorizedChar > 0)) {
                        valorizedChar++;
                        // reset
                        flushTimer = 0;

                        bufferFile.append(charRead); // concateniamo il contenuto di buffer con il carattere letto, bufferFile.length() = dim bufferFile + 2(dovuti a inputReader.read())
                        if (bufferFile.toString().compareTo("quit") == 0) {
                            end = true;
                            break;
                        }

                        if (bufferFile.length() == packetSize || (charRead == '\n' && bufferFile.length() >= 3)) {
                            P2pChatARQ.sendPacket(talkSocket, receiverIP, receiverPort, talkNumPacket, bufferFile.toString(), listenThread.getPrintStream(), verboseMode);
                            bufferFile = new StringBuilder();
                            talkNumPacket++;
                            if (talkNumPacket == 9999)
                                talkNumPacket = 1;    // se si arriva al numero massimo permesso dall'intestazione, rinizia

                            valorizedChar = 0;
                        }
                    }
                }

                listenThread.interrupt();
                //System.out.println("Press Ctrl + c to exit;");

                talkSocket.close(); // chiusura del socket
            } catch (SocketException e) {
                if (talkSocket != null) {
                    Logger.error("Opening talkSocket on the following port: " + talkSocket.getPort()/*talkPort*/ + " has failed", e);
                } else {
                    Logger.error("talkSocket is null", e);
                }
            }
        } catch (IOException e) {
            Logger.error("read failed", e);
        }
    }
}
