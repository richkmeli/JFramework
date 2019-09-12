package it.richkmeli.jframework.network.tcp.client.api.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// Riccardo Melioli
// Thread a cui e' delegata la gestione della comunicazione

class ConnectionServerThread extends Thread {
    Socket clientSocket;
    BufferedReader listenBuffer;
    boolean verboseMode;

    public ConnectionServerThread(Socket clientSocketParam, boolean verboseModeParam) {
        this.clientSocket = clientSocketParam;
        this.verboseMode = verboseModeParam;
    }

    @Override
    public void run() {
        if (verboseMode) System.out.println("THREAD: " + this.getName() + " Avviato");
        try {
            listenBuffer =
                    new BufferedReader( // legge il testo da uno stream di input
                            new InputStreamReader( // legge bytes e li decodifica in caratteri
                                    clientSocket.getInputStream()));

            String packetContent = "";
            Boolean isLast = false;
            while (!isLast && packetContent != null) {
                packetContent = listenBuffer.readLine();

                if (packetContent != null) {
                    if (verboseMode) {
                        System.out.print("received from: " + clientSocket.getInetAddress()
                                + " dalla porta " + clientSocket.getPort() + " -- : ");
                    }
                    System.out.println(packetContent);

                    // GESTIONE USCITA
                    if (packetContent.contains("quit"))
                        isLast = true;
                } else {
                    System.out.println("Errore : messaggio inviato corrotto(null)");
                }

            }
            listenBuffer.close();

        } catch (IOException e) {
            System.out.println("Errore di comunicazione nel thread "
                    + this.getName() + " dal client "
                    + clientSocket.getInetAddress() + " : "
                    + clientSocket.getPort());
        }
        if (verboseMode) System.out.println("\nTHREAD: " + this.getName() + " Terminato");
    }
}