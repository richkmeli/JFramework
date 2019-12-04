package it.richkmeli.jframework.network.tcp.server.http.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
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

public abstract class user {
    protected abstract void doSpecificAction(HttpServletRequest request) throws ServletException, DatabaseException;

    public void doGet(HttpServletRequest request, HttpServletResponse response, ServletManager servletManager) throws javax.servlet.ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            servletManager.doDefaultProcessRequest();
            ServletManager.checkLogin(request);

            // server session
            Session session = ServletManager.getServerSession(request);

            String user = session.getUser();
            boolean isAdmin = session.getAuthDatabaseManager().isAdmin(user);

            JSONObject messageJSON = new JSONObject();
            messageJSON.put("user", user);
            messageJSON.put("admin", isAdmin);

            response.addCookie(ServletManager.initSessionCookie(request));

            try {
                doSpecificAction(request);
            } catch (ServletException se) {
                Logger.error(se);
                throw se;
            }

            String output = servletManager.doDefaultProcessResponse(messageJSON.toString());
            out.println(new OKResponse(StatusCode.SUCCESS, output).json());

            out.flush();
            out.close();
        } catch (ServletException e) {
            out.println(e.getKOResponseJSON());
        } catch (DatabaseException e) {
            out.println((new KOResponse(StatusCode.DB_ERROR, e.getMessage())).json());
        } catch (Exception e) {
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }
        out.flush();
        out.close();
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response, ServletManager servletManager) throws javax.servlet.ServletException, IOException, ServletException {
        //if the code below is de-commented, this servlet disables DELETE
        //super.doDelete(req, resp);
        PrintWriter out = response.getWriter();

        try {
            Map<String, String> attribMap = servletManager.doDefaultProcessRequest();
            ServletManager.checkLogin(request);

            // server session
            Session session = ServletManager.getServerSession(request);
            String user = session.getUser();

            if (attribMap.containsKey("email")) {
                String payload = attribMap.get("email");

                if (session.getUser().equals(payload)) {
                    session.getAuthDatabaseManager().removeUser(payload);
                    session.removeUser();
                    out.println((new OKResponse(StatusCode.SUCCESS).json()));
                } else {
                    if (session.isAdmin()) {
                        session.getAuthDatabaseManager().removeUser(payload);
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

        } catch (Exception e) {
            // redirect to the JSP that handles errors
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage()).json()));
        }

    }

    private String GenerateUserListJSON(Session session) {
        //DatabaseManager databaseManager = session.getDatabaseManager();
        List<User> userList = new ArrayList<>();//databaseManager.refreshUser();
        userList.add(new User(session.getUser(), "hidden", session.isAdmin()));

        Type type = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();

        // oggetto -> gson
        String usersListJSON = gson.toJson(userList, type);

        return usersListJSON;
    }
}