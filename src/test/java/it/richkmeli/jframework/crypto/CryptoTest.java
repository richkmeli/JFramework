package it.richkmeli.jframework.crypto;


import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CryptoTest {
    private File secureDataClient;
    private File secureDataServer;
    private String clientID = "USER-001";
    private String serverKey = "testkeyServer";
    private String clientKey = "testkeyClient";
    private Crypto.Client client;
    private Crypto.Server server;

    private final static String[] cryptoStrings = {"01", "sdfs648df5", "-e.fe-w.f", "", "234pjojojojojojojojojojojojojojojojo234pi23pi4jpi234j2", "012345678", "{}+è\\/\\/\\/"};

    @Test
    public void crypto() {

        secureDataClient = new File("TEST1secureDataClient_" + Math.random() + ".txt");
        secureDataServer = new File("TEST1secureDataServer_" + Math.random() + ".txt");
        clientID = "USER-001";
        serverKey = "testkeyServer";
        clientKey = "testkeyClient";

        System.out.println("secureDataClient delete: " + secureDataClient.delete());
        System.out.println("secureDataServer delete: " + secureDataServer.delete());
        secureDataClient.deleteOnExit();
        secureDataServer.deleteOnExit();

        payloadExchange(secureDataClient, secureDataServer, clientKey, serverKey, clientID);

        encrypDecryptTest(client, server);

        secureDataClient.deleteOnExit();
        secureDataServer.deleteOnExit();
    }

    @Test
    public void cryptoMultiClient() {

        secureDataServer = new File("TEST1secureDataServer_" + Math.random() + ".txt");
        serverKey = "testkeyServer";
        clientKey = "testkeyClient";
        int numberOfClients = 10;

        for (int i = 0; i < numberOfClients; i++) {
            secureDataClient = new File("TEST1secureDataClient_" + Math.random() + ".txt");
            clientID = "USER-00" + i;
            payloadExchange(secureDataClient, secureDataServer, clientKey, serverKey, clientID);
            encrypDecryptTest(client, server);
            secureDataClient.deleteOnExit();
        }

        secureDataServer.deleteOnExit();
    }

    private static void encrypDecryptTest(Crypto.Client client, Crypto.Server server) {
        for (String plaintext : cryptoStrings) {
            // ** CLIENT **
            String encryptedPayloadClient = null;
            try {
                encryptedPayloadClient = client.encrypt(plaintext);
                // ** SERVER **
                String encryptedPayloadServer = server.encrypt(plaintext);
                String decryptedTextClient = server.decrypt(encryptedPayloadClient);
                assertEquals(plaintext, decryptedTextClient);
                // ** CLIENT **
                String decryptedTextServer = client.decrypt(encryptedPayloadServer);
                assertEquals(plaintext, decryptedTextServer);

            } catch (CryptoException e) {
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void cryptoAfterClientStateDeleteThenServerReset() {

        secureDataClient = new File("TEST2secureDataClient_" + Math.random() + ".txt");
        secureDataServer = new File("TEST2secureDataServer_" + Math.random() + ".txt");

        System.out.println("secureDataClient delete: " + secureDataClient.delete());
        System.out.println("secureDataServer delete: " + secureDataServer.delete());
        secureDataClient.deleteOnExit();
        secureDataServer.deleteOnExit();

        payloadExchange(secureDataClient, secureDataServer, clientKey, serverKey, clientID);
        encrypDecryptTest(client, server);

        // delete client state or simulating MITM scenario
        System.out.println("secureDataClient delete: " + secureDataClient.delete());
        if (secureDataClient.delete()) {
            System.out.println("secureDataClient delete: " + true);
        } else {
            secureDataClient.deleteOnExit();
            System.out.println("secureDataClient failed, ...reinit");
            secureDataClient = new File("TEST2secureDataClient_" + Math.random() + ".txt");
        }

        expectedClientServerStates(secureDataClient, secureDataServer, clientID, serverKey, clientKey, "", "", -1, 3);

        // reset server and client (to ensure there is not other session information) state
        server.deleteClientData();
        client.reset();

        // check if server is now able to establish a secure connection
        expectedClientServerStates(secureDataClient, secureDataServer, clientID, serverKey, clientKey, "", "", 3, 3);

        // server not aligned. TEST = VEVTVA==
        expectedClientServerStates(secureDataClient, secureDataServer, clientID, serverKey, clientKey, "VEVTVA==", "", -1, 3);

        secureDataClient.deleteOnExit();
        secureDataServer.deleteOnExit();
    }

    private void expectedClientServerStates(File secureDataClient, File secureDataServer, String clientID, String serverKey, String clientKey, String serverPayload, String clientPayload, int expectedClientState, int expectedServerState) {
        payloadExchange(secureDataClient, secureDataServer, clientKey, serverKey, clientID);

        String clientResponse = client.init(secureDataClient, clientKey, serverPayload);
        int clientState = new JSONObject(clientResponse).getInt("state");
        assertEquals(expectedClientState, clientState);

        String serverResponse = server.init(secureDataServer, serverKey, clientID, clientPayload);
        int serverState = new JSONObject(serverResponse).getInt("state");
        assertEquals(expectedServerState, serverState);
    }

    @Test
    public void cryptoAfterServerStateDeleteThenClientReset() {
        secureDataClient = new File("TEST3secureDataClient_" + Math.random() + ".txt");
        secureDataServer = new File("TEST3secureDataServer_" + Math.random() + ".txt");

        System.out.println("secureDataClient delete: " + secureDataClient.delete());
        System.out.println("secureDataServer delete: " + secureDataServer.delete());
        secureDataClient.deleteOnExit();
        secureDataServer.deleteOnExit();

        payloadExchange(secureDataClient, secureDataServer, clientKey, serverKey, clientID);
        encrypDecryptTest(client, server);

        // delete server state or simulating MITM scenario
        System.out.println("secureDataServer delete: " + secureDataServer.delete());
        if (secureDataServer.delete()) {
            System.out.println("secureDataServer delete: " + true);
        } else {
            secureDataServer.deleteOnExit();
            System.out.println("secureDataServer failed, ...reinit");
            secureDataServer = new File("TEST2secureDataServer_" + Math.random() + ".txt");
        }

        expectedClientServerStates(secureDataClient, secureDataServer, clientID, serverKey, clientKey, "", "", 3, -1);

        // reset Client state
        client.reset();

        // check if server is now able to establish a secure connection
        expectedClientServerStates(secureDataClient, secureDataServer, clientID, serverKey, clientKey, "", "", 3, 3);

        // server not aligned. TEST = VEVTVA==
        expectedClientServerStates(secureDataClient, secureDataServer, clientID, serverKey, clientKey, "VEVTVA==", "", -1, 3);

        secureDataClient.deleteOnExit();
        secureDataServer.deleteOnExit();
    }

    @Test
    public void encDecWithoutInit() {
        String plaintext = "test1234";

        Crypto.Client client = new Crypto.Client();
        // ** SERVER ** <-- clientResponse
        Crypto.Server server = new Crypto.Server();

        try {
            // ** CLIENT **
            String encryptedPayloadClient = client.encrypt(plaintext);
            // ** SERVER **
            String encryptedPayloadServer = server.encrypt(plaintext);
            String decryptedTextClient = server.decrypt(encryptedPayloadClient);
            assertEquals(""/*default of encrypt and decrypt error*/, decryptedTextClient);
            // ** CLIENT **
            String decryptedTextServer = client.decrypt(encryptedPayloadServer);
            assertEquals(""/*default of encrypt and decrypt error*/, decryptedTextServer);
        } catch (CryptoException e) {
            assertEquals("java.lang.Exception: encrypt, crypto not initialized, current state: 0", e.getMessage());
            //e.printStackTrace();
            //assert false;
        }
    }


    private void payloadExchange(File secureDataClient, File secureDataServer, String clientKey, String serverKey, String clientID) {
        // ** CLIENT **
        client = new Crypto.Client();
        // ** SERVER ** <-- clientResponse
        server = new Crypto.Server();

        // simulate a continuous exchange of payload until they reach the target state (3)
        String clientResponse = "";
        String serverResponse = "";
        int clientState = 0;
        int serverState = 0;
        String clientPayload = "";
        String serverPayload = "";

        int i = 0;
        do {
            i++;
            // ** CLIENT **
            clientResponse = client.init(secureDataClient, clientKey, serverPayload);
            clientState = new JSONObject(clientResponse).getInt("state");
            clientPayload = new JSONObject(clientResponse).getString("payload");
            System.out.println("Client State :" + clientState);
            System.out.println("Client Init " + i + ": " + clientResponse);

            // ** SERVER ** <-- clientResponse
            serverResponse = server.init(secureDataServer, serverKey, clientID, clientPayload);
            serverState = new JSONObject(serverResponse).getInt("state");
            serverPayload = new JSONObject(serverResponse).getString("payload");
            System.out.println("Client State :" + clientState);
            System.out.println("Server Init " + i + ": " + serverResponse);
        } while ((i < 10) && (clientState != 3 || serverState != 3));

    }

    @Test
    public void passwordTest() {
        for (String s : cryptoStrings) {
            // password for DB
            String dbPW = Crypto.hashPassword(s, false);

            // password for login
            String loginPW = Crypto.hashPassword(s, true);

            assertTrue(Crypto.verifyPassword(dbPW, loginPW));
        }
    }

    @Test
    public void putAndGetData() {
        File datafile = new File("datafile" + Math.random() + ".txt");
        for (String s : cryptoStrings) {
            Crypto.putData(datafile, "test", s, s);
            String retrievedData = Crypto.getData(datafile, "test", s);
            assertEquals(s, retrievedData);
        }
        datafile.delete();
        datafile.deleteOnExit();
    }


}




       /* @Test
        public void crypto2() {

            //String serverURL = "http://localhost:8080/";
            File secureDataClient = new File("secureDataClient.txt");
            File secureDataServer = new File("secureDataServer.txt");
            String clientID = "USER-001";
            String serverKey = "testkeyServer";
            String clientKey = "testkeyClient";
            String plaintext = "test1234";


            // ** CLIENT **
            //boolean responseInitC
            Crypto.Client client = new Crypto.Client();
            //System.out.println("Client Init: " + responseInitC);

            boolean responseInitC = client.init(serverURL, secureDataClient, serverKey); // TODO fare parte network
            //client.initOffline(); // TODO init offline che usa certificato precedentemente generato, non DH
            System.out.println("Client Init: " + responseInitC);

            String encryptedPayload = client.encrypt(plaintext);

            //String response = client.send(plaintext,10); // TODO fare parte network


            // ** SERVER **
            Crypto.Server server = new Crypto.Server();

            boolean responseInitS = server.init(secureDataServer, clientID, clientKey);
            System.out.println("Server Init: " + responseInitS);

            String decryptedText = server.decrypt(encryptedPayload);

            assertEquals(plaintext, decryptedText);

            String plaintext2 = "test2";
            server.encrypt(plaintext2);

            // ** CLIENT **

            String decryptedText2 = client.decrypt(plaintext2);

            assertEquals(plaintext2, decryptedText2);


        }*/



