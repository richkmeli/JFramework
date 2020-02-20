package it.richkmeli.jframework.network.tcp.server.http.util;


import it.richkmeli.jframework.crypto.Crypto;

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

