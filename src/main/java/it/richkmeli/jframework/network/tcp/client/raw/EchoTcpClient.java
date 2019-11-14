package it.richkmeli.jframework.network.tcp.client.raw;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


// Riccardo Melioli

public class EchoTcpClient {
    public static void main(String[] args) {// PARAMETRI
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

        // CLIENT SOCKET
        Socket talkSocket = null; //(tuo client) socket per mandare(receiverPort) pacchetti al destinatario e ricevere(talkPacket) l'ACK

        // INFORMAZIONI SULLA COMUNICAZIONE
        PrintWriter talkBuffer;
        BufferedReader bufferedReader;

        try {    // blocco per risorsa talk Socket

            try {    // Apertura socket per l'ascolto
                if (verboseMode) System.out.print("Connessione con: " + receiverIP + " : " + receiverPort + " ...");
                talkSocket = new Socket(receiverIP, receiverPort);    // acquisizione risorsa talk socket
                if (verboseMode) System.out.println("Stabilita");
            } catch (IOException e) {
                throw new IOException("Apertura del socket di invio sulla porta di invio " + receiverPort + " fallita");
            } // apertura socket sulla porta

            try {
                talkBuffer =
                        new PrintWriter( // codifica(formatta) gli oggetti in testo adatto per lo stream
                                new BufferedWriter( // scrive nello stream di output
                                        new OutputStreamWriter( // converte i caratteri dello stream in byte
                                                talkSocket.getOutputStream())), true); // autoflush = true

                bufferedReader =
                        new BufferedReader(    // crea un buffer di caratteri
                                new InputStreamReader(    // legge i byte dal terminale e li codifica in caratteri
                                        System.in));    // input da mandare al server

                String bufferFile;
                boolean end = false;
                while (!end) {
                    bufferFile = bufferedReader.readLine();    // legge dal terminale
                    talkBuffer.println(bufferFile);        // scrive nello stream di output cio che stato letto dal terminale

                    System.out.println("Sent to: " + receiverIP + " dalla porta " + receiverPort + " -- : " + bufferFile);
                    if (bufferFile.contains("quit")) {
                        end = true;
                    }

                }

                bufferedReader.close();
                talkBuffer.close();

            } catch (IOException e) {
                if (verboseMode) System.out.print("Connessione con: " + receiverIP + " : " + receiverPort + " ...");
                talkSocket.close();
                if (verboseMode) System.out.print("Conclusa");
                throw new IOException("Exception sui Buffer di comunicazione");
            }

            try {
                if (verboseMode) System.out.print("Connessione con: " + receiverIP + " : " + receiverPort + " ...");
                talkSocket.close(); // Chiusura Socket
                if (verboseMode) System.out.print("Conclusa");
            } catch (IOException e) {
                throw new IOException("Chiusura del socket di invio sulla porta di invio " + talkSocket.getInetAddress() + " fallita");
            } // chiusura del socket

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void help() {
        System.out.println("echoTcpClient"
                + "\n--------------------------------"
                + "\nUSAGE: echoTcpClient [-i <ipAddress(default: localhost)>][-p <port(default: 8000)>] [-v verbose(default: false)]"
                + "\nechoTcpClient [-p <Port(default: 8000)>] [-v verbose(default: false)]"
                + "\nechoTcpClient [-i <ipAddress(default: localhost)>]"
                + "\nechoTcpClient [-p <Port(default: 8000)>]"
                + "\nechoTcpClient [-v verbose(default: false)]"
                + "\nechoTcpClient [-h help]"
                + "\n--------------------------------"
                + "\n -l <port> : Talk port"
                + "\n -h : Show this guide"
                + "\n -v : Verbose mode");
    }

    private static Map<String, String> parameter(String[] args) {
        Map<String, String> param = new HashMap<String, String>();
        // OPZIONI DI DEFAULT
        param.put("receiverIP", "127.0.0.1");
        param.put("receiverPort", "8000");
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
            case 2:        // 2 parametri, comando piu argomento
                if ((args[0].compareTo("-p")) == 0) {
                    if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 65535) {
                        param.put("receiverPort", args[1]);
                    } else {
                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
                        help();
                    }
                } else if ((args[0].compareTo("-i")) == 0) {
                    param.put("receiverIP", args[1]);
                } else {
                    System.out.println("Parametri Errati");
                    help();
                    System.exit(1);
                }
                break;
            case 3:
                if ((args[0].compareTo("-p")) == 0) {
                    if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 65535) {
                        param.put("receiverPort", args[1]);
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
            case 5:
                if ((args[0].compareTo("-i")) == 0) {
                    param.put("receiverIP", args[1]);
                    if ((args[2].compareTo("-p")) == 0) {
                        if (Integer.parseInt(args[3]) > 0 && Integer.parseInt(args[3]) < 65535) {
                            param.put("receiverPort", args[1]);
                            if ((args[4].compareTo("-v")) == 0) {
                                param.put("verbose", "true");
                            }
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
            default:
                System.out.println("Parametri Errati");
                help();
                System.exit(1);
        }
        return param;
    }
}
