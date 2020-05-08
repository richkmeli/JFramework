package it.richkmeli.jframework.auth.web.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.auth.AuthDatabaseModel;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.model.exception.ModelException;
import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.auth.web.util.AuthStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OkResponse;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.log.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UserJob {
    protected abstract void doSpecificAction(AuthServletManager authServletManager) throws JServletException, DatabaseException;

    /**
     * get user info
     * @param authServletManager
     */

    public void doGet(AuthServletManager authServletManager)  {
        try {
            authServletManager.doDefaultProcessRequest();
            authServletManager.checkLogin();

            // server authSession
            AuthSession authSession = authServletManager.getAuthServerSession();

            String user = authSession.getUserID();
            boolean isAdmin = authSession.getAuthDatabaseManager().isAdmin(user);

            JSONObject messageJSON = new JSONObject();
            messageJSON.put("user", user);
            messageJSON.put("admin", isAdmin);

            authServletManager.initSessionCookie();

            try {
                doSpecificAction(authServletManager);
            } catch (JServletException se) {
                Logger.error(se);
                throw se;
            }

            String output = authServletManager.doDefaultProcessResponse(messageJSON.toString());
            authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS, output));

        } catch (JServletException e) {
            authServletManager.print(e.getResponse());
        } catch (DatabaseException e) {
            authServletManager.print(new KoResponse(AuthStatusCode.DB_ERROR, e.getMessage()));
        } catch (Throwable e) {
            authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage()));
        }

    }

    /**
     * remove user
     * @param authServletManager
     */

    public void doDelete(AuthServletManager authServletManager)  {
        //if the code below is de-commented, this servlet disables DELETE
        //super.doDelete(req, resp);
        try {
            Map<String, String> attribMap = authServletManager.doDefaultProcessRequest();
            authServletManager.checkLogin();

            // server authSession
            AuthSession authSession = authServletManager.getAuthServerSession();
            String user = authSession.getUserID();

            if (attribMap.containsKey("email")) {
                String payload = attribMap.get("email");

                if (authSession.getUserID().equals(payload)) {
                    authSession.getAuthDatabaseManager().removeUser(payload);
                    authSession.removeUser();
                    authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS,"User "+payload+" Removed"));
                } else {
                    if (authSession.isAdmin()) {
                        authSession.getAuthDatabaseManager().removeUser(payload);
                        authServletManager.print(new OkResponse(AuthStatusCode.SUCCESS,"User "+payload+" deleted. Admin right (admin: " + authSession.getUserID()+")"));
                    } else {
                        authServletManager.print(new KoResponse(AuthStatusCode.NOT_AUTHORIZED, "The current user is not authorized"));
                    }
                }
            } else {
                authServletManager.print(new KoResponse(AuthStatusCode.MISSING_FIELD));
            }


        } catch (JServletException e) {
            authServletManager.print(e.getResponse());
        } catch (DatabaseException e) {
            authServletManager.print(new KoResponse(AuthStatusCode.DB_ERROR, e.getMessage()));
        } catch (Exception e) {
            authServletManager.print(new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage()));
        }

    }

    private String GenerateUserListJSON(AuthSession authSession) throws ModelException {
        //DatabaseManager databaseManager = authSession.getDatabaseManager();
        List<User> userList = new ArrayList<>();//databaseManager.refreshUser();
        userList.add(new User(authSession.getUserID(), "hidden", authSession.isAdmin()));

        Type type = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();

        // oggetto -> gson
        String usersListJSON = gson.toJson(userList, type);

        return usersListJSON;
    }
}