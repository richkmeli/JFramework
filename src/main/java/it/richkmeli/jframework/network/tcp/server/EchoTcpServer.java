package it.richkmeli.jframework.network.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

// Riccardo Melioli
// Server concorrente gestito come Server multiprocesso / multithread. 
// Il processo padre quando riceve una una nuova richiesta apre un nuovo processo ( o thread) a cui passa il nuovo file descriptor delegandogli 
// la gestione della comunicazione.

public class EchoTcpServer {
    public static boolean end = false;

    public static void main(String[] args) {
        // PARAMETRI
        Map<String, String> param = parameter(args);
        final boolean verboseMode = Boolean.parseBoolean(param.get("verbose"));

        // INFORMAZIONI DEL NOSTRO SERVER
        ServerSocket listenSocket = null;
        final int listenPort = Integer.parseInt(param.get("listenPort"));    // porta su cui riceviamo i messaggi

        // INFORMAZIONI DEL CLIENT RICHIEDENTE LA CONNESSIONE
        Socket clientSocket = null;

        try {    // blocco per risorsa listen Socket

            try {    // Apertura socket per l'ascolto
                listenSocket = new ServerSocket(listenPort);    // acquisizione risorsa listen socket
            } catch (IOException e) {
                throw new IOException("Apertura del socket di ascolto sulla porta di ascolto " + listenPort + " fallita");
            } // apertura socket sulla porta


            while (!end) {

                try {    // blocco per risorsa client socket attesa connessione
                    if (verboseMode) System.out.println("SERVER: Server in attesa...");
                    clientSocket = listenSocket.accept();
                    System.out.println("SERVER: Connessione stabilita con : " + clientSocket.getInetAddress() + " sulla porta : " + clientSocket.getPort());
                } catch (IOException e) {
                    listenSocket.close();    // rilascio risorsa listen socket
                    throw new IOException("Client Socket Exception");
                }    // bloccante, attende una nuova connessione

                ConnectionServerThread threadServer = new ConnectionServerThread(clientSocket, verboseMode);    // avviamo il server di ascolto sulla listenport
                if (verboseMode) System.out.println("SERVER: Avvio Thread...");
                threadServer.start();
            }
            try {
                listenSocket.close();    // Chiusura Socket
            } catch (IOException e) {
                throw new IOException("Chiusura del socket di ascolto sulla porta di ascolto " + listenPort + " fallita");
            } // chiusura del socket

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void help() {
        System.out.println("echoTcpServer"
                + "\n--------------------------------"
                + "\nUSAGE: echoTcpServer [-l <listenPort(default: 8000)>] [-v verbose(default: false)]"
                + "\nUSAGE: echoTcpServer [-v verbose(default: false)]"
                + "\nechoTcpServer [-h help]"
                + "\n--------------------------------"
                + "\n -l <listenPort> : Listen port"
                + "\n -h : Show this guide"
                + "\n -v : Verbose mode");
    }

    private static Map<String, String> parameter(String[] args) {
        Map<String, String> param = new HashMap<String, String>();
        // OPZIONI DI DEFAULT
        param.put("listenPort", "8000");
        param.put("verbose", "false");

        switch (args.length) {

            case 0:
                break;//Nessun parametro passato, tutto di default
            case 1:        // 1 parametro
                if ((args[0].compareTo("-h")) == 0) {
                    help();
                    System.exit(1);
                } else if ((args[0].compareTo("-v")) == 0) {
                    param.put("verbose", "true");
                }
                break;
            case 2:        // 2 parametri
                if ((args[0].compareTo("-l")) == 0) {
                    if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 65535) {
                        param.put("listenPort", args[1]);
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
            case 3:
                if ((args[0].compareTo("-l")) == 0) {
                    if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 65535) {
                        param.put("listenPort", args[1]);
                        if ((args[2].compareTo("-v")) == 0) {
                            param.put("verbose", "true");
                        }
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
