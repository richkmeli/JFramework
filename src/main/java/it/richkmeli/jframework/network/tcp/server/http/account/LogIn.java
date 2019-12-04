package it.richkmeli.jframework.network.tcp.server.http.account;

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
import java.util.Map;

public abstract class LogIn {

    protected abstract void doSpecificAction(HttpServletRequest request) throws ServletException, DatabaseException;

    public void doAction(HttpServletRequest request, HttpServletResponse response, ServletManager servletManager) throws javax.servlet.ServletException, IOException {
        Session session = null;
        PrintWriter out = response.getWriter();

        try {
            session = servletManager.getServerSession();

            // check if is not already logged
            if (session.getUser() == null) {
                Map<String, String> attribMap = servletManager.doDefaultProcessRequest(false);/*ServletManager.extractParameters(request);
                /*Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);*/

                String email = attribMap.get("email");// = request.getParameter("email");
                String pass = attribMap.get("password");
                ;// = request.getParameter("password");

                if (session.getAuthDatabaseManager().isUserPresent(email)) {
                    boolean isAdmin = session.getAuthDatabaseManager().isAdmin(email);
                    if (session.getAuthDatabaseManager().checkPassword(email, pass)) {

                        // set userID into the session
                        session.setUser(email);
                        session.setAdmin(isAdmin);

                        response.addCookie(ServletManager.initSessionCookie(request));

                        try {
                            doSpecificAction(request);
                        } catch (ServletException se) {
                            Logger.error(se);
                            throw se;
                        }

                        JSONObject adminInfo = new JSONObject();
                        adminInfo.put("admin", isAdmin);

                        //String output = adminInfo.toString();
                        String output = servletManager.doDefaultProcessResponse(adminInfo.toString());

                        out.println((new OKResponse(StatusCode.SUCCESS, output)).json());
                    } else {
                        // pass sbagliata
                        out.println((new KOResponse(StatusCode.WRONG_PASSWORD)).json());
                    }
                } else {
                    Logger.error("User: " + email + " not found in AuthDatabase.");
                    // mail non trovata
                    out.println((new KOResponse(StatusCode.ACCOUNT_NOT_FOUND, "user: " + request.getAttribute("email") + "; password: " + request.getAttribute("password"))).json());
                }
            } else {
                // already logged
                out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
            }
        } catch (ServletException e) {
            session.setUser(null);
            if (e.getMessage().contains("java.lang.Exception: decrypt, crypto not initialized, current state: 0")) {
                out.println((new KOResponse(StatusCode.SECURE_CONNECTION, e.getMessage())).json());
            } else {
                out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
            }
        } catch (Exception e) {
            session.setUser(null);
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
