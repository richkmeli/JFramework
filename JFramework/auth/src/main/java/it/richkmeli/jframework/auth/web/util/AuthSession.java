package it.richkmeli.jframework.auth.web.util;

import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.log.Logger;

/**
 * Authenticated Servlet Manager
 * do not add static modifier to these fields, because this respective ServletManager has a static object of this class
 */

public class AuthSession extends Session {
    private AuthDatabaseManager authDatabaseManager;
    private String userID;      //user from AuthSchema
    private Boolean isAdmin;

    public AuthSession() throws DatabaseException {
        super();
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = null;
    }

    public AuthSession(Session session) throws DatabaseException {
        super(session);
        authDatabaseManager = new AuthDatabaseManager();
        userID = null;
        isAdmin = null;
    }

    public AuthSession(AuthSession authSession) {
        super();
        authDatabaseManager = authSession.authDatabaseManager;
        userID = authSession.userID;
        isAdmin = authSession.isAdmin;
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
            Logger.info("init AuthDatabase");
            authDatabaseManager = new AuthDatabaseManager();
        }
        return authDatabaseManager;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
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
                } catch (DatabaseException | ModelException e) {
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

