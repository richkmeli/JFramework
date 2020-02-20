package it.richkmeli.jframework.auth.web.util;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;

public class AuthSession extends Session {
    private AuthDatabaseManager authDatabaseManager;
    private String userID;      //user from AuthSchema
    private Boolean isAdmin;

    public AuthSession() throws DatabaseException {
        super();
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = false;
    }

    public AuthSession(Session session) throws DatabaseException {
        super(session);
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = false;
    }

    public AuthSession(AuthSession authSession, Session session) {
        super(session);
        authDatabaseManager = authSession.authDatabaseManager;
        userID = authSession.userID;
        isAdmin = authSession.isAdmin;
    }


    public AuthDatabaseManager getAuthDatabaseManager() throws DatabaseException {
        //Logger.i("authDatabaseManager" + authDatabaseManager);
        if (authDatabaseManager == null) {
            authDatabaseManager = new AuthDatabaseManager();
        }
        return authDatabaseManager;
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


}

