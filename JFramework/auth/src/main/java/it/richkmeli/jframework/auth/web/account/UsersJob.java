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
import it.richkmeli.jframework.util.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;


public abstract class UsersJob {

    protected abstract void doSpecificAction(HttpServletRequest request) throws JServletException, DatabaseException;

    public void doGet(HttpServletRequest request, HttpServletResponse response, AuthServletManager authServletManager) throws IOException {
        PrintWriter out = response.getWriter();

        try {
            authServletManager.doDefaultProcessRequest();
            AuthServletManager.checkLogin(request);
            AuthSession session = AuthServletManager.getAuthServerSession(request);

            try {
                doSpecificAction(request);
            } catch (JServletException se) {
                Logger.error(se);
                throw se;
            }

            if (session.isAdmin()) {
                String output = authServletManager.doDefaultProcessResponse(GenerateUsersListJSON(session));
                out.println(new OkResponse(AuthStatusCode.SUCCESS, output).json());

                out.flush();
                out.close();
            } else {
                out.println((new KoResponse(AuthStatusCode.NOT_AUTHORIZED, "The current user is not authorized").json()));
            }


        } catch (JServletException e) {
            out.println(e.getKoResponseJSON());
        } catch (DatabaseException e) {
            out.println((new KoResponse(AuthStatusCode.DB_ERROR, e.getMessage())).json());
        } catch (Exception e) {
            out.println((new KoResponse(AuthStatusCode.GENERIC_ERROR, e.getMessage())).json());
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