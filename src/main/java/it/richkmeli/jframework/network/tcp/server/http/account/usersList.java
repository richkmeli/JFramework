package it.richkmeli.jframework.network.tcp.server.http.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.auth.AuthDatabaseManager;
import it.richkmeli.jframework.auth.model.User;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.OKResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.StatusCode;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletException;
import it.richkmeli.jframework.network.tcp.server.http.util.ServletManager;
import it.richkmeli.jframework.network.tcp.server.http.util.Session;
import it.richkmeli.jframework.orm.DatabaseException;
import it.richkmeli.jframework.util.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;


public abstract class usersList {

    protected abstract void doSpecificAction(HttpServletRequest request) throws ServletException, DatabaseException;

    public void doGet(HttpServletRequest request, HttpServletResponse response, ServletManager servletManager) throws javax.servlet.ServletException, IOException, ServletException {
        PrintWriter out = response.getWriter();

        try {
            servletManager.doDefaultProcessRequest();
            ServletManager.checkLogin(request);
            Session session = ServletManager.getServerSession(request);

           /* boolean encryption = false;
            if (request.getParameterMap().containsKey("channel")) {
                if ("rmc".equalsIgnoreCase(request.getParameter("channel"))) {
                    encryption = true;
                }
            }*/

            try {
                doSpecificAction(request);
            } catch (ServletException se) {
                Logger.error(se);
                throw se;
            }

            if (session.isAdmin()) {
                String output = servletManager.doDefaultProcessResponse(GenerateUsersListJSON(session));
                out.println(new OKResponse(StatusCode.SUCCESS, output).json());

      /*          if (encryption) {  // RMC
                    String encPayload = session.getCryptoServer().encrypt(GenerateUsersListJSON(session));
                    out.println((new OKResponse(StatusCode.SUCCESS, encPayload)).json());
                } else {  // WEBAPP
                    // Authentication
                    out.println((new OKResponse(StatusCode.SUCCESS, GenerateUsersListJSON(session)).json()));
                }
*/
                out.flush();
                out.close();
            } else {
                out.println((new KOResponse(StatusCode.NOT_AUTHORIZED, "The current user is not authorized").json()));
            }


        } catch (ServletException e) {
            out.println(e.getKOResponseJSON());
        } catch (Exception e) {
            // redirect to the JSP that handles errors
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage()).json()));
        }
    }

    private static String GenerateUsersListJSON(Session session) throws DatabaseException {
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