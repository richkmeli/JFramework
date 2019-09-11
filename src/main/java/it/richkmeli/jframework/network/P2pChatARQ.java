package it.richkmeli.jframework.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

//Riccardo Melioli
//09/12/2015

public class P2pChatARQ {
    public static void main(String[] args) {
        // PARAMETRI
        Map<String, String> param = parameter(args);
        final boolean verboseMode = Boolean.parseBoolean(param.get("verbose"));

        // INFORMAZIONI DESTINATARIO A CUI SCRIVERE
        InetAddress receiverIP = null;
        try {    // blocco per host
            receiverIP = InetAddress.getByName(param.get("receiverIP"));
        } catch (UnknownHostException e) {
            System.out.println("Host Sconosciuto");
            help();
        } // indirizzo del server(destinatario)
        int receiverPort = Integer.parseInt(param.get("receiverPort"));    // porta dell'indirizzo del server(destinatario)

        // INFORMAZIONI DEL NOSTRO PEER
        final int listenPort = Integer.parseInt(param.get("listenPort"));    // porta su cui riceviamo i messaggi
        //int talkPort = 9000; // porta in uscita del nostro client. (opzionale)

        // INFORMAZIONI SULLA COMUNICAZIONE
        final int packetDim = 10; // dimensione pacchetti in cui suddividere il testo della comunicazione
        BufferedReader inputReader;
        String bufferFile = "";

        // CONNESSIONE TALK
        DatagramSocket talkSocket = null; //(tuo client) socket per mandare(receiverPort) pacchetti al destinatario e ricevere(talkPacket) l'ACK
        byte[] talkBuffer = new byte[packetDim + 4];    // NumeroPacchetto(4)+ContenutoPacchetto(packetDim), i 4 in piu sono per la gestione pacchetti
        int talkNumPacket = 0;    // per gestione numero pacchetti, e per ACK di ritorno


        // Server di ricezione messaggi gestito da thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // CONNESSIONE LISTEN
                    DatagramSocket listenSocket = new DatagramSocket(listenPort);    // apertura socket sulla porta
                    byte[] listenBuffer = new byte[packetDim + 4];    // NumeroPacchetto(4)+ContenutoPacchetto(packetDim), i 4 in piu sono per la gestione pacchetti
                    DatagramPacket listenPacket = new DatagramPacket(listenBuffer, listenBuffer.length);    // pacchetto a datagramma
                    int listenNumPacket = 0;    // per gestione numero pacchetti, e per ACK di ritorno

                    Boolean isLast = false;
                    while (!isLast) {
                        listenSocket.receive(listenPacket);

                        String packetContent = new String(listenPacket.getData(), 0, listenPacket.getLength()); //(byte[] byteArray, int offset, int count), count= dimensione pacchetto ricevuto
                        listenNumPacket = Integer.parseInt(packetContent.substring(0, 4)); // primi 4 caratteri utilizzati per la numerazione
                        // GESTIONE PACCHETTI IN ARRIVO
                        String infoPacket = packetContent.substring(4, packetContent.length());
                        System.out.print(infoPacket);

                        if (verboseMode)
                            System.out.println("\t\t Riceived from: " + listenPacket.getAddress() + " : " + listenPacket.getPort());
                        // INVIO ACK DI AVVENUTA RICEZIONE
                        listenBuffer = ("ACK" + String.format("%04d", listenNumPacket)).getBytes();    // ACK per secondo pacchetto con formato: ACK0002
                        DatagramPacket packetACK = new DatagramPacket(listenBuffer, listenBuffer.length, listenPacket.getAddress(), listenPacket.getPort());    // mandi l'ACK sulla porta che ti ha mandato il dato
                        listenSocket.send(packetACK);
                    }
                    listenSocket.close(); // chiusura del socket

                } catch (SocketException e) {
                    System.out.println("Apertura del socket di ascolto sulla porta di ascolto " + listenPort + " fallita");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // invio messagg in modo interattivo
        try {
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            try {    // blocco per socket
                talkSocket = new DatagramSocket(/*talkPort, omettendo viene generata random*/);    // apertura socket

                thread.start();
                if (verboseMode) System.out.println("Thread Listening Started...");

                boolean end = false;
                while (!end) {
                    int read;
                    read = inputReader.read();
                    char charRead = (char) read;

                    bufferFile = bufferFile + charRead; // concateniamo il contenuto di buffer con il carattere letto, bufferFile.length() = dim bufferFile + 2(dovuti a inputReader.read())
                    if (bufferFile.compareTo("quit") == 0) {
                        end = true;
                        break;
                    }

                    if (bufferFile.length() == packetDim || (charRead == '\n' && bufferFile.length() >= 3)) {
                        sendPacket(talkBuffer, receiverIP, receiverPort, talkSocket, talkNumPacket, bufferFile, verboseMode);
                        bufferFile = "";
                        talkNumPacket++;
                        if (talkNumPacket == 9999)
                            talkNumPacket = 1;    // se si arriva al numero massimo permesso dall'intestazione, rinizia
                    }
                }

                thread.interrupt();
                if (verboseMode) System.out.println("Thread Listening Stopped...");
                System.out.println("Press Ctrl + c to exit;");

                talkSocket.close(); // chiusura del socket
            } catch (SocketException e) {
                System.out.println("Apertura del socket di scrittura sulla porta di scrittura " + talkSocket.getPort()/*talkPort*/ + " fallita");
            }
        } catch (IOException e) {
            System.out.println("Lettura Fallita");
        }
    }

    private static void sendPacket(byte[] buffer, InetAddress receiverIP, int receiverPort, DatagramSocket socket, int numPacket, String bufferFile, boolean verboseMode) throws IOException {
        String composedString = String.format("%04d", numPacket) + bufferFile;    // stringa formata da intestazione e dati
        buffer = composedString.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverIP, receiverPort);
        socket.send(packet);    // invio pacchetto
        if (verboseMode) System.out.print("\t\tSend : " + numPacket);

        // INFORMAZIONI DELL'ACK MANDATO DAL SERVER
        String ackPacket;
        int ackNum = 0;
        do {
            try {
                socket.setSoTimeout(2000);    // TEMPORIZZAZIONE: lancia eccezione(SocketTimeoutException) se non si riceve nulla in 2 secondi
                socket.receive(packet); // se non riceviamo riscontro(ACK) dal server continuamo ad inviare
                ackPacket = new String(packet.getData(), 0, packet.getLength()); //(byte[] byteArray, int offset, int count), count= dimensione pacchetto ricevuto
                ackNum = Integer.parseInt(ackPacket.substring(3, ackPacket.length()));  // ack della forma: ACK0002 , cioe "ACK" + numPacket
                if (verboseMode) System.out.println("\tReceived: " + ackPacket);
            } catch (SocketTimeoutException e) {
                socket.send(packet);    // quando viene lanciata eccezione rinvio pacchetto
                if (verboseMode) System.out.print("\tOver timeout...\nresend : " + numPacket);
            }
        } while (ackNum != numPacket);
    }


    private static void help() {
        System.out.println("Peer-to-Peer Chat ARQ"
                + "\n--------------------------------"
                + "\nUSAGE: p2pChatARQ <ReceiverIP(default: localhost)> [-p <port(8000)>]"
                + "\np2pChatARQ <ReceiverIP>"
                + "\np2pChatARQ <ReceiverIP> [-p <port>]"
                + "\np2pChatARQ <ReceiverIP> [-p <port>] [-a <listenPort(8000)>]"
                + "\np2pChatARQ <ReceiverIP> [-p <port>] [-a <listenPort(8000)>] [-v verbose(false)]"
                + "\np2pChatARQ [-h help]"
                + "\n--------------------------------"
                + "\n -p <port> : Receiver port"
                + "\n -a <listenPort>: Porta d'ascolto"
                + "\n -h : Show this guide"
                + "\n -v : Verbose mode");
    }

    private static Map<String, String> parameter(String[] args) {
        Map<String, String> param = new HashMap<String, String>();
        // OPZIONI DI DEFAULT
        param.put("receiverIP", "localhost");
        param.put("receiverPort", "8000");
        param.put("listenPort", "8000");
        param.put("verbose", "false");

        switch (args.length) {

            case 0:
                break;//Nessun parametro passato, tutto di default
            case 1:
                if ((args[0].compareTo("-h")) == 0) {
                    help();
                    System.exit(1);
                } else {
                    param.put("receiverIP", args[0]);
                }
                break;
            case 3:
                if (args[1].compareTo("-p") == 0) {
                    param.put("receiverIP", args[0]);
                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 65535) {
                        param.put("receiverPort", args[2]);
                    } else {
                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
                        help();
                    }
                } else {
                    System.out.println("Parametri Errati");
                    help();
                    System.exit(1);
                }
                break;
            case 5:
                if (args[1].compareTo("-p") == 0 && args[3].compareTo("-a") == 0) {
                    param.put("receiverIP", args[0]);
                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 65535) {
                        param.put("receiverPort", args[2]);
                    } else {
                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
                        help();
                    }
                    if (Integer.parseInt(args[4]) > 0 && Integer.parseInt(args[4]) < 65535) {
                        param.put("listenPort", args[4]);
                    } else {
                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
                        help();
                    }
                } else {
                    System.out.println("Parametri Errati");
                    help();
                    System.exit(1);
                }
                break;
            case 6:
                if (args[1].compareTo("-p") == 0 && args[3].compareTo("-a") == 0 && args[5].compareTo("-v") == 0) {
                    param.put("receiverIP", args[0]);
                    param.put("verbose", "true");
                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 65535) {
                        param.put("receiverPort", args[2]);
                    } else {
                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
                        help();
                    }
                    if (Integer.parseInt(args[4]) > 0 && Integer.parseInt(args[4]) < 65535) {
                        param.put("listenPort", args[4]);
                    } else {
                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
                        help();
                    }
                } else {
                    System.out.println("Parametri Errati");
                    help();
                    System.exit(1);
                }
                break;
            default:
                System.out.println("Parametri Errati");
                help();
                System.exit(1);
        }
        return param;
    }
}