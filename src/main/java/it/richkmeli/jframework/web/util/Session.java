package it.richkmeli.jframework.web.util;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.orm.DatabaseException;

public class Session {
    private AuthDatabaseManager authDatabaseManager;
    private String userID;      //user from AuthSchema
    private String rmcID;       //client id from RichkwareSchema
    private Boolean isAdmin;
    private Crypto.Server cryptoServer;

    public Session() throws DatabaseException {
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = false;
        cryptoServer = new Crypto.Server();
    }

    public Session(Session session) throws DatabaseException {
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = false;
        cryptoServer = new Crypto.Server();
    }


    public AuthDatabaseManager getAuthDatabaseManager() throws DatabaseException {
        //Logger.i("authDatabaseManager" + authDatabaseManager);
        if (authDatabaseManager != null) {
            return authDatabaseManager;
        } else {
            authDatabaseManager = new AuthDatabaseManager();
            return authDatabaseManager;
        }
    }

    public void setRmcID(String rmcID) {
        this.rmcID = rmcID;
    }

    public String getUser() {
        return userID;
    }

    public void setUser(String userID) {
        this.userID = userID;
    }

    public void removeUser() {
        this.userID = null;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Crypto.Server getCryptoServer() {
        return cryptoServer;
    }


}

