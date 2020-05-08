package it.richkmeli.jframework.auth.web.util;

import it.richkmeli.jframework.auth.AuthDatabaseModel;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.log.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Authenticated Servlet Manager
 * do not add static modifier to these fields, because this respective ServletManager has a static object of this class
 */

public class AuthSession extends Session {
    private AuthDatabaseModel authDatabaseManager;
    private String userID;      //user from AuthSchema
    private Boolean isAdmin;

    public AuthSession(AuthDatabaseModel authDatabaseManager) throws DatabaseException {
        super();
        this.authDatabaseManager = authDatabaseManager;//new AuthDatabaseJframeworkManager();
        userID = null;
        isAdmin = null;
    }

    public AuthSession(AuthDatabaseModel authDatabaseManager, Session session) throws DatabaseException {
        super(session);
        this.authDatabaseManager = authDatabaseManager;//new AuthDatabaseJframeworkManager();
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

    public AuthDatabaseModel getAuthDatabaseManager(/*AuthDatabaseModel authDatabaseModel*/) throws DatabaseException {
        //Logger.i("authDatabaseManager" + authDatabaseManager);
        if (authDatabaseManager == null) {
            Logger.info("authDatabaseManager is null, init AuthDatabase");
            //authDatabaseManager = authDatabaseModel;
        }
        return authDatabaseManager;
    }

   /* public <T extends AuthDatabaseModel> AuthDatabaseModel getAuthDatabaseManager(Class authDatabaseManagerClass) throws DatabaseException {
        //Logger.i("authDatabaseManager" + authDatabaseManager);
        if (authDatabaseManager == null) {
            Logger.info("init AuthDatabase");
            // search constructor
            T obj;
            try {
                Constructor<T> constructor = authDatabaseManagerClass.getConstructor();
                obj = constructor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new DatabaseException("Auth, constructor in class '" + authDatabaseManagerClass.getCanonicalName()+"' is not present");
            }
            authDatabaseManager = obj;
        }
        return authDatabaseManager;
    }*/

    /*public AuthDatabaseJframeworkManager getAuthDatabaseManager() throws DatabaseException {
        //Logger.i("authDatabaseManager" + authDatabaseManager);
        if (authDatabaseManager == null) {
            Logger.info("init AuthDatabase");
            authDatabaseManager = new AuthDatabaseJframeworkManager();
        }
        return authDatabaseManager;
    }*/

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

