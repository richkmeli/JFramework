//package it.richkmeli.jframework.system;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ArgsManager {
//    // TODO rendi parametrico
//
//
////    public static void main(String[] args) {
////        // PARAMETRI
////        Map<String, String> param = parameter(args);
////        final boolean verboseMode = Boolean.parseBoolean(param.get("verbose"));
////
////        // INFORMAZIONI DESTINATARIO A CUI SCRIVERE
////        InetAddress receiverIP = null;
////        try {    // blocco per host
////            receiverIP = InetAddress.getByName(param.get("receiverIP"));
////        } catch (UnknownHostException e) {
////            System.out.println("Host Sconosciuto");
////            help();
////        } // indirizzo del server(destinatario)
////        int receiverPort = Integer.parseInt(param.get("receiverPort"));    // porta dell'indirizzo del server(destinatario)
////
////        // INFORMAZIONI DEL NOSTRO PEER
////        final int listenPort = Integer.parseInt(param.get("listenPort"));    // porta su cui riceviamo i messaggi
////        //int talkPort = 9000; // porta in uscita del nostro client. (opzionale)
////    }
//
//
//    private static void help() {
//        System.out.println("Peer-to-Peer Chat ARQ"
//                + "\n--------------------------------"
//                + "\nUSAGE: p2pChatARQ <ReceiverIP(default: localhost)> [-p <port(8000)>]"
//                + "\np2pChatARQ <ReceiverIP>"
//                + "\np2pChatARQ <ReceiverIP> [-p <port>]"
//                + "\np2pChatARQ <ReceiverIP> [-p <port>] [-a <listenPort(8000)>]"
//                + "\np2pChatARQ <ReceiverIP> [-p <port>] [-a <listenPort(8000)>] [-v verbose(false)]"
//                + "\np2pChatARQ [-h help]"
//                + "\n--------------------------------"
//                + "\n -p <port> : Receiver port"
//                + "\n -a <listenPort>: Porta d'ascolto"
//                + "\n -h : Show this guide"
//                + "\n -v : Verbose mode");
//    }
//
//    private static Map<String, String> parameter(String[] args) {
//        Map<String, String> param = new HashMap<String, String>();
//        // OPZIONI DI DEFAULT
//        param.put("receiverIP", "localhost");
//        param.put("receiverPort", "8000");
//        param.put("listenPort", "8000");
//        param.put("verbose", "false");
//
//        switch (args.length) {
//
//            case 0:
//                break;//Nessun parametro passato, tutto di default
//            case 1:
//                if ((args[0].compareTo("-h")) == 0) {
//                    help();
//                    System.exit(1);
//                } else {
//                    param.put("receiverIP", args[0]);
//                }
//                break;
//            case 3:
//                if (args[1].compareTo("-p") == 0) {
//                    param.put("receiverIP", args[0]);
//                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 65535) {
//                        param.put("receiverPort", args[2]);
//                    } else {
//                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
//                        help();
//                    }
//                } else {
//                    System.out.println("Parametri Errati");
//                    help();
//                    System.exit(1);
//                }
//                break;
//            case 5:
//                if (args[1].compareTo("-p") == 0 && args[3].compareTo("-a") == 0) {
//                    param.put("receiverIP", args[0]);
//                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 65535) {
//                        param.put("receiverPort", args[2]);
//                    } else {
//                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
//                        help();
//                    }
//                    if (Integer.parseInt(args[4]) > 0 && Integer.parseInt(args[4]) < 65535) {
//                        param.put("listenPort", args[4]);
//                    } else {
//                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
//                        help();
//                    }
//                } else {
//                    System.out.println("Parametri Errati");
//                    help();
//                    System.exit(1);
//                }
//                break;
//            case 6:
//                if (args[1].compareTo("-p") == 0 && args[3].compareTo("-a") == 0 && args[5].compareTo("-v") == 0) {
//                    param.put("receiverIP", args[0]);
//                    param.put("verbose", "true");
//                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 65535) {
//                        param.put("receiverPort", args[2]);
//                    } else {
//                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
//                        help();
//                    }
//                    if (Integer.parseInt(args[4]) > 0 && Integer.parseInt(args[4]) < 65535) {
//                        param.put("listenPort", args[4]);
//                    } else {
//                        System.out.println("Porta non nel range ammissibile [0 <-> 65535]");
//                        help();
//                    }
//                } else {
//                    System.out.println("Parametri Errati");
//                    help();
//                    System.exit(1);
//                }
//                break;
//            default:
//                System.out.println("Parametri Errati");
//                help();
//                System.exit(1);
//        }
//        return param;
//    }
//}
