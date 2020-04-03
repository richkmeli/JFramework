package it.richkmeli.jframework.auth.web.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.log.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public abstract class UsersJob {

    protected abstract void doSpecificAction(AuthServletManager authServletManager) throws JServletException, DatabaseException;

    public void doGet(AuthServletManager authServletManager){
        try {
            Map<String, String> attribMap = authServletManager.doDefaultProcessRequest();
            authServletManager.checkLogin();

            // server authSession
            AuthSession authSession = authServletManager.getAuthServerSession();

            try {
                doSpecificAction(authServletManager);
            } catch (JServletException se) {
                Logger.error(se);
                throw se;
            }

            if (authSession.isAdmin()) {
                String output = authServletManager.doDefaultProcessResponse(GenerateUsersListJSON(authSession));
                authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS, output));
            } else {
                authServletManager.print(new KoResponse(AuthStatusCode.NOT_AUTHORIZED, "The current user is not authorized"));
            }

        } catch (JServletException e) {
            authServletManager.print(e.getResponse());
        } catch (DatabaseException e) {
            authServletManager.print(new KoResponse(AuthStatusCode.DB_ERROR, e.getMessage()));
        } catch (Throwable e) {
            authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage()));
        }
    }

    private static String GenerateUsersListJSON(AuthSession session) throws DatabaseException {
        AuthDatabaseManager authDatabaseManager = session.getAuthDatabaseManager();
        List<User> userList = authDatabaseManager.getAllUsers();

        Type type = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();

        // oggetto -> gson
        String usersListJSON = gson.toJson(userList, type);

        return usersListJSON;
    }
}