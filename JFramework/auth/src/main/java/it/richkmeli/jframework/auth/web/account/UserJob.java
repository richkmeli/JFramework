package it.richkmeli.jframework.auth.web.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.auth.web.util.AuthServletManager;
import it.richkmeli.jframework.auth.web.util.AuthSession;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.JServletException;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UserJob {
    protected abstract void doSpecificAction(HttpServletRequest request) throws JServletException, DatabaseException;

    public void doGet(HttpServletRequest request, HttpServletResponse response, AuthServletManager servletManager) throws javax.servlet.ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            servletManager.doDefaultProcessRequest();
            AuthServletManager.checkLogin(request);

            // server authSession
            AuthSession authSession = AuthServletManager.getAuthServerSession(request);

            String user = authSession.getUser();
            boolean isAdmin = authSession.getAuthDatabaseManager().isAdmin(user);

            JSONObject messageJSON = new JSONObject();
            messageJSON.put("user", user);
            messageJSON.put("admin", isAdmin);

            AuthServletManager.initSessionCookie(request, response);

            try {
                doSpecificAction(request);
            } catch (JServletException se) {
                Logger.error(se);
                throw se;
            }

            String output = servletManager.doDefaultProcessResponse(messageJSON.toString());
            out.println(new OKResponse(StatusCode.SUCCESS, output).json());

            out.flush();
            out.close();
        } catch (JServletException e) {
            out.println(e.getKOResponseJSON());
        } catch (DatabaseException e) {
            out.println((new KOResponse(StatusCode.DB_ERROR, e.getMessage())).json());
        } catch (Exception e) {
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }
        out.flush();
        out.close();
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response, AuthServletManager servletManager) throws IOException {
        //if the code below is de-commented, this servlet disables DELETE
        //super.doDelete(req, resp);
        PrintWriter out = response.getWriter();

        try {
            Map<String, String> attribMap = servletManager.doDefaultProcessRequest();
            AuthServletManager.checkLogin(request);

            // server authSession
            AuthSession authSession = AuthServletManager.getAuthServerSession(request);
            String user = authSession.getUser();

            if (attribMap.containsKey("email")) {
                String payload = attribMap.get("email");

                if (authSession.getUser().equals(payload)) {
                    authSession.getAuthDatabaseManager().removeUser(payload);
                    authSession.removeUser();
                    out.println((new OKResponse(StatusCode.SUCCESS).json()));
                } else {
                    if (authSession.isAdmin()) {
                        authSession.getAuthDatabaseManager().removeUser(payload);
                        out.println((new OKResponse(StatusCode.SUCCESS).json()));
                    } else {
                        out.println((new KOResponse(StatusCode.NOT_AUTHORIZED, "The current user is not authorized").json()));
                    }
                }
            } else {
                out.println((new KOResponse(StatusCode.MISSING_FIELD).json()));
            }
            out.flush();
            out.close();

        } catch (JServletException e) {
            out.println(e.getKOResponseJSON());
        } catch (DatabaseException e) {
            out.println((new KOResponse(StatusCode.DB_ERROR, e.getMessage())).json());
        } catch (Exception e) {
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

    }

    private String GenerateUserListJSON(AuthSession authSession) {
        //DatabaseManager databaseManager = authSession.getDatabaseManager();
        List<User> userList = new ArrayList<>();//databaseManager.refreshUser();
        userList.add(new User(authSession.getUser(), "hidden", authSession.isAdmin()));

        Type type = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();

        // oggetto -> gson
        String usersListJSON = gson.toJson(userList, type);

        return usersListJSON;
    }
}