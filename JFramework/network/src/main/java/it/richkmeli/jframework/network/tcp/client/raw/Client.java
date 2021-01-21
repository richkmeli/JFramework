package it.richkmeli.jframework.network.tcp.client.raw;

import it.richkmeli.jframework.network.util.CommunicationLock;
import it.richkmeli.jframework.util.log.Logger;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final CommunicationLock communicationLock = new CommunicationLock();
    private static Socket clientSocket;

    public static String sendPacket(String serverAddress, int port, String packet) {
        String response = null;
        try {
            clientSocket = new Socket(serverAddress, port);
            startListenThread();

            send(packet);
            response = communicationLock.getMessage();

            clientSocket.close();
        } catch (IOException e) {
            Logger.error(e);
        }
        return response;
    }

    /**
     * use Client.send() passing clientSocket to send packet anc communicationLock.getMessage() to get the response
     * @param serverAddress
     * @param port
     * @param task
     */
    public static void doTask(String serverAddress, int port, ClientTask task) {
        try {
            clientSocket = new Socket(serverAddress, port);
            startListenThread();

            //send(packet);
            //response = communicationLock.getMessage();
            task.doStuff(clientSocket, communicationLock);

            clientSocket.close();
        } catch (IOException e) {
            Logger.error(e);
        }
    }


    public static void send(Socket clientSocket, String message) throws IOException {
        //send = return (\n)
        message += "\n";
        //Logger.info(message);
        //Send Data to the Server
        OutputStream output = clientSocket.getOutputStream();
        byte[] data = message.getBytes();
        output.write(data);
        output.flush();
        //PrintWriter writer = new PrintWriter(output, true);
        //writer.println(message);
    }

    private static void send(String message) throws IOException {
        send(clientSocket, message);
    }


    private static void startListenThread() {
        final Thread inThread = new Thread() {
            @Override
            public void run() {
                // Use a Scanner to read from the remote server

                Scanner in = null;
                try {
                    InputStream input = clientSocket.getInputStream();
                    //input.read();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    int character;
                    while ((character = reader.read()) != -1) {
                        //Logger.info(""+(char) character);
                        communicationLock.append((char) character);
                    }
                } catch (IOException e) {
                    Logger.error(e);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        };
        inThread.start();
    }


//    private void richkRequest(String ServerAddress, int port, String message) {
//        try {
//            DatagramSocket socket = new DatagramSocket();
//            InetAddress IPAddress = InetAddress.getByName(ServerAddress);
//
//            DatagramPacket out = new DatagramPacket(message.getBytes(), message.length(), IPAddress, port);
//            socket.send(out);
//
//            byte[] response = new byte[8000];
//            //Definisco il pacchetto che riceverò dal server
//            DatagramPacket in = new DatagramPacket(response, response.length);
//            System.out.println("receiving..... ");
//            // response timeout
//            socket.setSoTimeout(5000);
//            String stringaRicevuta = null;
//            try {
//                socket.receive(in);
//                stringaRicevuta = new String(in.getData());
//
//                System.out.println("message ricevuto: " + stringaRicevuta);
//            } catch (SocketTimeoutException ste) {
//                System.out.println("Timeout - Pacchetto perso");
//            }
//
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
