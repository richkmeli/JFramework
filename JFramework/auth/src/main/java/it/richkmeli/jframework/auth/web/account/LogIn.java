package it.richkmeli.jframework.auth.web.account;

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
import java.util.Map;

public abstract class LogIn {

    protected abstract void doSpecificAction(HttpServletRequest request, HttpServletResponse response) throws JServletException, DatabaseException;

    public void doAction(HttpServletRequest request, HttpServletResponse response, AuthServletManager authServletManager) throws javax.servlet.ServletException, IOException {
        AuthSession authSession = null;
        PrintWriter out = response.getWriter();

        try {
            authSession = authServletManager.getAuthServerSession();

            // check if is not already logged
            if (authSession.getUser() == null) {
                Map<String, String> attribMap = authServletManager.doDefaultProcessRequest(false);/*ServletManager.extractParameters(request);
                /*Map<String, String> attribMap = ServletManager.doDefaultProcessRequest(request);*/

                String email = attribMap.get("email");// = request.getParameter("email");
                String pass = attribMap.get("password");
                // = request.getParameter("password");

                if (authSession.getAuthDatabaseManager().isUserPresent(email)) {
                    boolean isAdmin = authSession.getAuthDatabaseManager().isAdmin(email);
                    if (authSession.getAuthDatabaseManager().checkPassword(email, pass)) {

                        // set userID into the session
                        authSession.setUser(email);
                        authSession.setAdmin(isAdmin);

                        AuthServletManager.initSessionCookie(request, response);

                        try {
                            doSpecificAction(request, response);
                        } catch (JServletException se) {
                            Logger.error(se);
                            throw se;
                        }

                        JSONObject adminInfo = new JSONObject();
                        adminInfo.put("admin", isAdmin);

                        //String output = adminInfo.toString();
                        String output = authServletManager.doDefaultProcessResponse(adminInfo.toString());

                        out.println((new OKResponse(StatusCode.SUCCESS, output)).json());
                    } else {
                        // pass sbagliata
                        out.println((new KOResponse(StatusCode.WRONG_PASSWORD)).json());
                    }
                } else {
                    Logger.error("User: " + email + " not found in AuthDatabase.");
                    // mail non trovata
                    out.println((new KOResponse(StatusCode.ACCOUNT_NOT_FOUND /*"user: " + request.getAttribute("email") + "; password: " + request.getAttribute("password")*/)).json());
                }
            } else {
                // already logged
                out.println((new KOResponse(StatusCode.ALREADY_LOGGED)).json());
            }
        } catch (JServletException e) {
            if (authSession != null) {
                authSession.setUser(null);
            }
            if (e.getMessage() != null) {
                if (e.getMessage().contains("java.lang.Exception: decrypt, crypto not initialized, current state: 0")) {
                    out.println((new KOResponse(StatusCode.SECURE_CONNECTION, e.getMessage())).json());
                } else {
                    out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
                }
            } else {
                // if the message is Servlet exception is empty, show generic error message
                out.println((new KOResponse(StatusCode.GENERIC_ERROR)).json());
            }
        } catch (Exception e) {
            if (authSession != null) {
                authSession.setUser(null);
            }
            out.println((new KOResponse(StatusCode.GENERIC_ERROR, e.getMessage())).json());
        }

        out.flush();
        out.close();

    }
}
