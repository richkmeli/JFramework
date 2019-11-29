package it.richkmeli.jframework.network.tcp.server.http.util;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;

public class Session {
    private AuthDatabaseManager authDatabaseManager;
    private String userID;      //user from AuthSchema
    private Boolean isAdmin;
    private Crypto.Server cryptoServer;

    public Session() throws DatabaseException {
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = false;
        cryptoServer = new Crypto.Server();
    }

    public Session(Session session) {
        authDatabaseManager = session.authDatabaseManager;
        userID = session.userID;
        isAdmin = session.isAdmin;
        cryptoServer = session.cryptoServer;
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
        if (isAdmin == null) {
            if (userID != null) {
                try {
                    isAdmin = authDatabaseManager.isAdmin(userID);
                } catch (DatabaseException e) {
                    Logger.error("isAdmin", e);
                    return false;
                }
                return isAdmin;
            } else {
                Logger.error("isAdmin, userID is null");
                return false;
            }
        } else {
            return isAdmin;
        }
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Crypto.Server getCryptoServer() {
        return cryptoServer;
    }


}

