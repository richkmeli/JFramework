package it.richkmeli.jframework.network.tcp.client.api.client.raw;

import it.richkmeli.jframework.network.util.CommunicationLock;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static Socket clientSocket;



    public static void start(String serverAddress, int port) {
        String message = "";

        //richkRequest(serverAddress,port,message);
        try {
            clientSocket = new Socket(serverAddress, port);
            startListenThread();

            String response = CommunicationLock.getMessage();
            send("2");
            response = CommunicationLock.getMessage();

            while (CommunicationLock.isFinished()) {
                send("1");
                response = CommunicationLock.getMessage();
            }


            Thread.sleep(5000);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /*
    String response = getMessage();

            if ("".equalsIgnoreCase(response)) {
                System.err.println("response is empty");
            } else {
                ////////////////////// intToHex
                int value = Integer.parseInt(response.substring(response.indexOf("<<") + 2, response.indexOf(">>")));

                send("0x" + Integer.toHexString(value) + "\n");

                //response = read(clientSocket);
                response = getMessage();
                /////////////////////// hexToAscii
                String svalue = response.substring(response.indexOf("<<") + 2, response.indexOf(">>"));

                send(hexToAscii(svalue) + "\n");
                response = getMessage();

                ///////////////////////
                svalue = response.substring(response.indexOf("<<") + 2, response.indexOf(">>"));
                String[] strings = svalue.split(" ");
                String str = "";
                for (String s : strings) {
                    //int i = Integer.parseInt(s);
                    Integer iOctal = Integer.parseInt(s, 8);
                   // Cast decimal to its corresponding ASCII value.
                    char cOctal = (char)iOctal.intValue();
                    str += (char) cOctal;
                }
                send(str + "\n");
                Thread.sleep(200);
                response = getMessage();
            }
            */


    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private static void send(String message) throws IOException {
        //invio = a capo
        message += "\n";
        System.out.print(message);
        //Send Data to the Server
        OutputStream output = clientSocket.getOutputStream();
        byte[] data = message.getBytes();
        output.write(data);
        output.flush();
        //PrintWriter writer = new PrintWriter(output, true);
        //writer.println(message);
    }

    /* private static String read(Socket clientSocket) throws IOException {
         //Read Data from the Server
         InputStream input = clientSocket.getInputStream();
         //input.read();
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         int character;
         StringBuilder dataS = new StringBuilder();
         // while ((character = reader.read()) != -1) {
         while (reader.ready()) {
             //System.out.print((char) character);
             //dataS.append((char) character);
             dataS.append(reader.readLine()).append("\n");
         }
         return dataS.toString();
     }
 */
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
                        System.out.print((char) character);
                        CommunicationLock.append((char) character);
                    }
                } catch (Exception e) {
//					e.printStackTrace();
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }

            ;
        };
        inThread.start();
    }


    private void richkRequest(String ServerAddress, int port, String message) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(ServerAddress);

            DatagramPacket out = new DatagramPacket(message.getBytes(), message.length(), IPAddress, port);
            socket.send(out);

            byte[] response = new byte[8000];
            //Definisco il pacchetto che riceverò dal server
            DatagramPacket in = new DatagramPacket(response, response.length);
            System.out.println("receiving..... ");
            // response timeout
            socket.setSoTimeout(5000);
            String stringaRicevuta = null;
            try {
                socket.receive(in);
                stringaRicevuta = new String(in.getData());

                System.out.println("message ricevuto: " + stringaRicevuta);
            } catch (SocketTimeoutException ste) {
                System.out.println("Timeout - Pacchetto perso");
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
