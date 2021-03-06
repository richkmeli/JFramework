package it.richkmeli.jframework.network.tcp.server.http.util;


import it.richkmeli.jframework.crypto.Crypto;

/**
 * do not add static modifier to these fields, because this respective ServletManager has a static object of this class
 */

public class Session {
    private Crypto.Server cryptoServer;

    public Session() {
        cryptoServer = new Crypto.Server();
    }

    public Session(Session session) {
        cryptoServer = session.getCryptoServer();
    }

    public Crypto.Server getCryptoServer() {
        return cryptoServer;
    }

    public void setCryptoServer(Crypto.Server cryptoServer) {
        this.cryptoServer = cryptoServer;
    }
}

